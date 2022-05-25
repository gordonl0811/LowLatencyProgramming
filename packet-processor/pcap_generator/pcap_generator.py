from scapy.all import wrpcap
from scapy.layers.inet import Ether, IP, TCP


def generate_pcap(output):
    pkt = Ether() / IP() / TCP()
    wrpcap(output, pkt, append=True)


