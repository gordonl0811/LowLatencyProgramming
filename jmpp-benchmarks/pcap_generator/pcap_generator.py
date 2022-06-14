from scapy.all import wrpcap
from scapy.layers.inet import Ether, IP, TCP, UDP

from random import randint, uniform


def generate_tcp_packet():

    return Ether() / IP() / TCP()


def generate_udp_packet():
    return Ether() / IP() / UDP()


def generate_random_packets(output_file, packet_count):

    open(output_file, "w").close()

    for i in range(packet_count):

        tcp_chance = uniform(0, 1)

        if tcp_chance > 0.5:
            packet = generate_tcp_packet()
        else:
            packet = generate_udp_packet()

        wrpcap(
            output_file,
            packet,
            append=True
        )


if __name__ == "__main__":
    generate_random_packets("input_1000000.pcap", 1000000)
    # generate_random_packets("input_multiple.pcap", 100)
