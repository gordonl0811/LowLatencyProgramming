package PacketProcessor;

import PacketProcessor.QueuePacketProcessor.FilterAndDropQueueProcessor;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        FilterAndDropQueueProcessor processor = new FilterAndDropQueueProcessor(
                1024,
                "src/main/resources/input_thousand.pcap"
        );

        processor.initialize();
        processor.start();

    }
}
