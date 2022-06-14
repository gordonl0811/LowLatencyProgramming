# Java Modular Packet Processor

A lightweight Java library for processing network packets.

## Documentation

- Overview
- User Guide
- Benchmarks
- Future Work

## Overview

The Java Modular Packet Processor (JMPP) is part of a Final Year Project (Programming Strategies for Low-Latency Applications) for the Computing BEng at Imperial College London.

JMPP allows users to create variations of `Processors`, consisting of `Components` that perform different operations on packets. These components are interchangeable, allowing users to specify a network of components to route packets through with a graph-like structure.

The project is not built for production, but instead has a broader objective that aligns with the purpose of the university project. Packets are passed between components using the [LMAX Disruptor](https://lmax-exchange.github.io/disruptor/), a sophisticated data structure designed to replace queues used in typical producer-consumer design patterns. JMPP contains reproducible benchmarks (collected using the Java Microbenchmark Harness) that demonstrate the advantages of the Disruptor over the high-performing `java.util.concurrent.ArrayBlockingQueue`.

JMPP is also built on the lightweight [pkts.io Java library developed by aboutsip](https://github.com/aboutsip/pkts).

## User Guide

## Benchmarks

## Future Work

