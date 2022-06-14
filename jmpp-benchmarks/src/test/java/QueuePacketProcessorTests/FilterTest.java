package QueuePacketProcessorTests;

public class FilterTest {

//    @Rule
//    public TemporaryFolder tempFolder = new TemporaryFolder();
//
//    @Test
//    public void testFilterThreadTerminatesWithPoisonPacket()
//            throws IOException, InterruptedException, ExecutionException, TimeoutException {
//
//        final String source = "src/test/resources/PacketProducerTest/input_single.pcap";
//
//        BlockingQueue<Packet> producerQueue = new ArrayBlockingQueue<>(1000);
//        BlockingQueue<Packet> dummyQueueOne = new ArrayBlockingQueue<>(1000);
//        BlockingQueue<Packet> dummyQueueTwo = new ArrayBlockingQueue<>(1000);
//
//        Reader reader = new Reader(source, producerQueue);
//        Filter filter = new Filter(producerQueue, dummyQueueOne, dummyQueueTwo);
//
//        new Thread(reader).start();
//
//        // Check that the thread has completed, i.e. terminated
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        Future<?> future = executor.submit(filter);
//        future.get(5, TimeUnit.SECONDS);
//    }
//
//    @Test
//    public void testFilterThreadForwardsTwoPoisonPackets()
//        throws IOException, InterruptedException {
//
//        final String source = "src/test/resources/PacketProducerTest/input_single.pcap";
//
//        BlockingQueue<Packet> producerQueue = new ArrayBlockingQueue<>(1000);
//        BlockingQueue<Packet> dummyQueueOne = new ArrayBlockingQueue<>(1000);
//        BlockingQueue<Packet> dummyQueueTwo = new ArrayBlockingQueue<>(1000);
//
//        Reader reader = new Reader(source, producerQueue);
//        Filter filter = new Filter(producerQueue, dummyQueueOne, dummyQueueTwo);
//
//        Thread producerThread = new Thread(reader);
//        Thread filterThread = new Thread(filter);
//
//        producerThread.start();
//        filterThread.start();
//
//        producerThread.join();
//        filterThread.join();
//
//        // Check that the thread has passed two PoisonPackets
//        List<Packet> dummyPacketsOne = new ArrayList<>();
//        dummyQueueOne.drainTo(dummyPacketsOne);
//        assert(dummyPacketsOne.get(dummyPacketsOne.size() - 1) instanceof PoisonPacket);
//
//        List<Packet> dummyPacketsTwo = new ArrayList<>();
//        dummyQueueTwo.drainTo(dummyPacketsTwo);
//        assert(dummyPacketsTwo.get(dummyPacketsTwo.size() - 1) instanceof PoisonPacket);
//
//    }
//
//    @Test
//    public void testFilterSplitsTcpUdpPackets() throws IOException, InterruptedException {
//        BlockingQueue<Packet> producerQueue = new ArrayBlockingQueue<>(1000);
//        BlockingQueue<Packet> tcpQueue = new ArrayBlockingQueue<>(1000);
//        BlockingQueue<Packet> udpQueue = new ArrayBlockingQueue<>(1000);
//
//        final String source = "src/test/resources/PacketFilterTest/input_multiple.pcap";
//
//        Reader reader = new Reader(source, producerQueue);
//        Filter filter = new Filter(producerQueue, tcpQueue, udpQueue);
//
//        Thread producerThread = new Thread(reader);
//        Thread filterThread = new Thread(filter);
//
//        producerThread.start();
//        filterThread.start();
//
//        producerThread.join();
//        filterThread.join();
//
//        // From WireShark, there are 52 TCP and 48 UDP packets
//        // A PoisonPacket is expected in the queue though, hence the +1
//
//        List<Packet> tcpPackets = new ArrayList<>();
//        tcpQueue.drainTo(tcpPackets);
//        assert(tcpPackets.size() == 52 + 1);
//
//        List<Packet> udpPackets = new ArrayList<>();
//        udpQueue.drainTo(udpPackets);
//        assert(udpPackets.size() == 48 + 1);
//
//    }
}
