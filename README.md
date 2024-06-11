# Ethan
![Static Badge](https://img.shields.io/badge/ethan-dev-blue) ![Static Badge](https://img.shields.io/badge/author-Huang%20Z.Y.-blue)
 ![GitHub top language](https://img.shields.io/github/languages/top/huangzy1218/ethan)
![GitHub last commit](https://img.shields.io/github/last-commit/huangzy1218/ethan?style=flat)


Ethan is a high-performance, Java-based RPC framework. It refers to the design of Apache Dubbo.

## Architecture

<img src="assets/architecture.png" alt="RPC Architecture" width="80%"/>

- **Registration Center** is a crucial component in an RPC (Remote Procedure Call) framework. It acts as a service
  directory where services register themselves with their metadata, such as service names, addresses, and protocols.
- **Server** hosts the actual implementation of remote procedures or services.
- **Client** is the entity that initiates requests to remote services provided by servers. 

