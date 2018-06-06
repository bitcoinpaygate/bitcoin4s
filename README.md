# bitcoin4s

This is a scala wrapper for the `bitcoin-rpc` interface.

[![Build Status](https://travis-ci.org/bitcoinpaygate/bitcoin4s.svg)](https://travis-ci.org/bitcoinpaygate/bitcoin4s)
[![Download](https://api.bintray.com/packages/bitcoinpaygate/bitcoinpaygate-maven/bitcoin4s/images/download.svg)](https://bintray.com/bitcoinpaygate/bitcoinpaygate-maven/bitcoin4s/_latestVersion)

# Features

Currently we implement a subset of `json-rpc` operations, including:

* `sendmany`
* `getwalletinfo`
* `estimatefee`
* `listunspent`
* `getnewaddress`
* `sendfrom`
* `sendtoaddress`
* `settxfee`
* `listsinceblock`
* `gettransaction`

Project is designed in a way that it's easy to add new operations any time.

# Testing

Run `sbt test` or run tests from your IDE


# Publishing

Currently using bintray.

when testing:
```
sbt publishLocal
```

real release:
```
sbt publish
```