## Hugecast is an add-on enabling Off-Heap In-Memory Format in Hazelcast 3, Community Edition.

It's a highly experimental project aiming to reduce GC-introduced latency when huge data-sets are used in Hazelcast.
It uses memory allocator from the Netty project, I take no credit for that. In the fact I probably introduced new bugs in the process of stripping-down the allocator.

It has been (poorly) tested against Hazelcast 3.1.2

### Usage
- Build it
- Put the resulting JAR on the classpath
- Now you can configure your maps to use Off-Heap In-Memory Format!

### Disclaimer
I take no responsibility if it eats your data. It's a research project, if you want to use Off-Heap In-Memory Format in production, then I recommend you to use Hazelcast Enterprise Edition instead!