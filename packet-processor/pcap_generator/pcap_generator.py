from scapy.all import wrpcap
from scapy.layers.inet import Ether, IP, TCP

from random import randint


def generate_tcp_mss_packet(min_mss, max_mss):

    packet = Ether() / IP() / TCP(options=[("MSS", randint(min_mss, max_mss))])

    return packet


def generate_random_packets(output_file, packet_count):

    for i in range(packet_count):
        wrpcap(
            output_file,
            generate_tcp_mss_packet(1400, 1600),
            append=True
        )


if __name__ == "__main__":
    # generate_random_packets("input.pcap", 1000)
    generate_random_packets("single_input.pcap", 1)
