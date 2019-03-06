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

# Testing on CircleCI

Circle CI runs both unit and integration tests

# Testing

Run `sbt test` or run tests from your IDE

# Integration testing

Integration tests require `bitcoind` running on `localhost` with `user=user`, `password=password` and on `port=18443`

for example you can run `regtest-bitcoind-cluster`:

```bash
docker run -p 18444:18444 -p 18443:18443 --rm bitcoinpaygate/regtest-bitcoind-cluster:0.17.1
```

after that run:

```bash
sbt it:test
```

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