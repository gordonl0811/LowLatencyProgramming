package PacketProcessor;

import PacketProcessor.DisruptorPacketProcessor.FilterAndDropDisruptorProcessor;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        FilterAndDropDisruptorProcessor processor = new FilterAndDropDisruptorProcessor(
                1024,
                "src/main/resources/input_thousand.pcap"
        );

        processor.initialize();
        processor.start();

    }
}
