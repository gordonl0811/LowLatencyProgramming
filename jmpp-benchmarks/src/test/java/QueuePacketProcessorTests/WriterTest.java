package QueuePacketProcessorTests;

public class WriterTest {

//  @Rule
//  public TemporaryFolder tempFolder = new TemporaryFolder();
//
//  @Test
//  public void testWriterThreadTerminatesWithPoisonPacket()
//      throws IOException, InterruptedException, ExecutionException, TimeoutException {
//
//    final String source = "src/test/resources/PacketWriterTest/input_single.pcap";
//    final File dest = tempFolder.newFile("output.pcap");
//    BlockingQueue<Packet> producerQueue = new ArrayBlockingQueue<>(1000);
//
//    Reader reader = new Reader(source, producerQueue);
//    Writer writer = new Writer(producerQueue, dest);
//
//    new Thread(reader).start();
//
//    // Check that the thread has completed, i.e. terminated
//    ExecutorService executor = Executors.newSingleThreadExecutor();
//    Future<?> future = executor.submit(writer);
//    future.get(5, TimeUnit.SECONDS);
//  }
//
//  @Test
//  public void testWriterWritesMultiplePacketsToPcap() throws IOException, InterruptedException {
//
//    final String source = "src/test/resources/PacketWriterTest/input_multiple.pcap";
//    final File dest = tempFolder.newFile("output.pcap");
//    BlockingQueue<Packet> producerQueue = new ArrayBlockingQueue<>(1000);
//
//    Reader reader = new Reader(source, producerQueue);
//    Writer writer = new Writer(producerQueue, dest);
//
//    Thread producerThread = new Thread(reader);
//    Thread writerThread = new Thread(writer);
//
//    producerThread.start();
//    producerThread.join();
//    writerThread.start();
//    writerThread.join();
//
//    // Check that each packet is the same
//    Pcap input = Pcap.openStream(source);
//    Pcap output = Pcap.openStream(dest);
//
//    // Build Lists for comparison
//    List<Packet> inputList = new ArrayList<>();
//    input.loop(inputList::add);
//
//    List<Packet> outputList = new ArrayList<>();
//    output.loop(outputList::add);
//
//    assert(inputList.size() == outputList.size());
//
//    // Check similarity by comparing ArrivalTime and Protocol
//    for (int i = 0; i < inputList.size(); i++) {
//      Packet inputPacket = inputList.get(i);
//      Packet outputPacket = outputList.get(i);
//      assert(inputPacket.getArrivalTime() == outputPacket.getArrivalTime());
//      assert(inputPacket.getProtocol() == outputPacket.getProtocol());
//    }
//
//  }
}
