# bitcoin4s

This is a scala wrapper for the `bitcoin-rpc` interface.

[![CircleCI](https://circleci.com/gh/bitcoinpaygate/bitcoin4s.svg?style=svg)](https://circleci.com/gh/bitcoinpaygate/bitcoin4s)
[![Download](https://api.bintray.com/packages/bitcoinpaygate/bitcoinpaygate-maven/bitcoin4s/images/download.svg)](https://bintray.com/bitcoinpaygate/bitcoinpaygate-maven/bitcoin4s/_latestVersion)

# Features

Currently we implement a subset of `json-rpc` operations, including:

* `getwalletinfo`
* `getnetworkinfo`
* `getmininginfo`
* `getmempoolinfo`
* `getblockchaininfo`
* `estimatesmartfee`
* `listunspent`
* `getnewaddress`
* `sendtoaddress`
* `settxfee`
* `generate`
* `gettransaction`
* `getrawtransaction`
* `listsinceblock`
* `sendmany`
* `createrawtransaction`
* `signrawtransaction`
* `sendrawtransaction`
* `validateaddress`

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