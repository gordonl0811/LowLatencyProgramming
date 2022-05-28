import io.pkts.Pcap;
import io.pkts.PcapOutputStream;
import io.pkts.frame.PcapGlobalHeader;
import io.pkts.packet.Packet;
import io.pkts.protocol.Protocol;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class PacketProcessor {

  static class PacketProducer implements Runnable {

    private final Pcap source;
    private final BlockingQueue<Packet> producerQueue;

    PacketProducer(String source, BlockingQueue<Packet> producerQueue) throws IOException {
      this.source = Pcap.openStream(source);
      this.producerQueue = producerQueue;
    }

    @Override
    public void run() {
      try {
        // Load the packets into the queue
        this.source.loop(packet -> {
          try {
            producerQueue.put(packet);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          return true;
        });

//        // Send a poison object to signal the queue closing
//        producerQueue.put();

      } catch (IOException e) {
        e.printStackTrace();
      }



    }

    static class PacketFilter implements Runnable {

      private final BlockingQueue<Packet> producerQueue;
      private final BlockingQueue<Packet> tcpQueue;
      private final BlockingQueue<Packet> udpQueue;

      PacketFilter(BlockingQueue<Packet> producerQueue,
          BlockingQueue<Packet> tcpQueue,
          BlockingQueue<Packet> udpQueue) {
        this.producerQueue = producerQueue;
        this.tcpQueue = tcpQueue;
        this.udpQueue = udpQueue;
      }

      private void filterPacket(Packet packet) throws IOException, InterruptedException {
        if (packet.hasProtocol(Protocol.TCP)) {
          tcpQueue.put(packet);
        } else if (packet.hasProtocol(Protocol.UDP)) {
          udpQueue.put(packet);
        }
      }

      @Override
      public void run() {
        try {
          while (!(Thread.currentThread().isInterrupted())) {
            filterPacket(producerQueue.take());
          }
          // Drain and process remaining elements on the queue
          final LinkedList<Packet> remainingPackets = new LinkedList<>();
          producerQueue.drainTo(remainingPackets);
          for(Packet complexObject : remainingPackets) {
            filterPacket(complexObject);
          }
        } catch (IOException | InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    static class TcpWriter implements Runnable {

      private final BlockingQueue<Packet> tcpQueue;
      private final PcapOutputStream output;

      TcpWriter(BlockingQueue<Packet> tcpQueue, String dest) throws FileNotFoundException {
        this.tcpQueue = tcpQueue;
        this.output = PcapOutputStream.create(
            PcapGlobalHeader.createDefaultHeader(),
            new FileOutputStream(dest)
        );
        ;
      }

      private void writeTcpPacket(Packet packet) throws IOException {
        output.write(packet);
      }

      @Override
      public void run() {
        try {
          while (!(Thread.currentThread().isInterrupted())) {
            writeTcpPacket(tcpQueue.take());
          }
          // Drain and process remaining elements on the queue
          final LinkedList<Packet> remainingPackets = new LinkedList<>();
          tcpQueue.drainTo(remainingPackets);
          for(Packet complexObject : remainingPackets) {
            writeTcpPacket(complexObject);
          }
        } catch (IOException | InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    static class UdpWriter implements Runnable {

      private final BlockingQueue<Packet> udpQueue;
      private final PcapOutputStream output;

      UdpWriter(BlockingQueue<Packet> udpQueue, String dest) throws FileNotFoundException {
        this.udpQueue = udpQueue;
        this.output = PcapOutputStream.create(
            PcapGlobalHeader.createDefaultHeader(),
            new FileOutputStream(dest)
        );
        ;
      }

      private void writeUdpPacket(Packet packet) throws IOException {
        output.write(packet);
      }

      @Override
      public void run() {
        try {
          while (!(Thread.currentThread().isInterrupted())) {
            writeUdpPacket(udpQueue.take());
          }
          // Drain and process remaining elements on the queue
          final LinkedList<Packet> remainingPackets = new LinkedList<>();
          udpQueue.drainTo(remainingPackets);
          for(Packet complexObject : remainingPackets) {
            writeUdpPacket(complexObject);
          }
        } catch (IOException | InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    public static void main(String[] args) throws IOException {

      BlockingQueue<Packet> producerQueue = new ArrayBlockingQueue<>(100);
      BlockingQueue<Packet> tcpQueue = new ArrayBlockingQueue<>(100);
      BlockingQueue<Packet> udpQueue = new ArrayBlockingQueue<>(100);

      final String source = "src/main/resources/input.pcap";
      final String tcpDest = "src/main/resources/tcp_output.pcap";
      final String udpDest = "src/main/resources/udp_output.pcap";

      PacketProducer packetProducer = new PacketProducer(source, producerQueue);
      PacketFilter packetFilter = new PacketFilter(producerQueue, tcpQueue, udpQueue);
      TcpWriter tcpWriter = new TcpWriter(tcpQueue, tcpDest);
      UdpWriter udpWriter = new UdpWriter(udpQueue, udpDest);

      new Thread(packetProducer).start();
      new Thread(packetFilter).start();
      new Thread(tcpWriter).start();
      new Thread(udpWriter).start();
    }

  }
}
