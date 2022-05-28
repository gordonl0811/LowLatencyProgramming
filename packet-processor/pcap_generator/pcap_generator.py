from scapy.all import wrpcap
from scapy.layers.inet import Ether, IP, TCP, UDP

from random import randint, uniform


def generate_tcp_mss_packet(min_mss, max_mss):

    packet = Ether() / IP() / TCP(options=[("MSS", randint(min_mss, max_mss))])

    return packet


def generate_udp_packet():
    return Ether() / IP()/ UDP()


def generate_random_packets(output_file, packet_count):

    tcp_count = 0

    for i in range(packet_count):

        tcp_chance = uniform(0, 1)

        if tcp_chance > 0.5:
            tcp_count += 1
            packet = generate_tcp_mss_packet(1400, 1600)
        else:
            packet = generate_udp_packet()

        wrpcap(
            output_file,
            packet,
            append=True
        )

    print(f"TCP Packets: {tcp_count}")
    print(f"UDP Packets: {packet_count - tcp_count}")


if __name__ == "__main__":
    generate_random_packets("input.pcap", 1000)
    # generate_random_packets("single_input.pcap", 1)
