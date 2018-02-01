package com.wlangiewicz.bitcoin4s

import spray.json._

object TestData {
  val walletInfoResponse: JsValue =
    """{
      |    "result": {
      |        "walletversion": 60000,
      |        "balance": 1.65751751,
      |        "unconfirmed_balance": 0.01,
      |        "immature_balance": 0,
      |        "txcount": 195,
      |        "keypoololdest": 1483630921,
      |        "keypoolsize": 100,
      |        "paytxfee": 0
      |    },
      |    "error": null,
      |    "id": null
      |}
      |""".stripMargin.parseJson

  val networkInfoResponse: JsValue =
    """
      |{
      |    "result": {
      |        "version": 130100,
      |        "subversion": "/Satoshi:0.13.1/",
      |        "protocolversion": 70014,
      |        "localservices": "000000000000000d",
      |        "localrelay": true,
      |        "timeoffset": 0,
      |        "connections": 8,
      |        "networks": [
      |            {
      |                "name": "ipv4",
      |                "limited": false,
      |                "reachable": true,
      |                "proxy": "",
      |                "proxy_randomize_credentials": false
      |            },
      |            {
      |                "name": "ipv6",
      |                "limited": false,
      |                "reachable": true,
      |                "proxy": "",
      |                "proxy_randomize_credentials": false
      |            },
      |            {
      |                "name": "onion",
      |                "limited": true,
      |                "reachable": false,
      |                "proxy": "",
      |                "proxy_randomize_credentials": false
      |            }
      |        ],
      |        "relayfee": 0.00001,
      |        "localaddresses": [
      |            {
      |                "address": "1.2.3.4",
      |                "port": 18333,
      |                "score": 1
      |            },
      |            {
      |                "address": "abcd:efef::abcde:adad:1234:5678",
      |                "port": 18333,
      |                "score": 1
      |            }
      |        ],
      |        "warnings": "Warning: unknown new rules activated (versionbit 28)"
      |    },
      |    "error": null,
      |    "id": null
      |}
    """.stripMargin.parseJson

  val miningInfoResponse =
    """
      |{
      |    "result": {
      |        "blocks": 1089632,
      |        "currentblocksize": 0,
      |        "currentblockweight": 0,
      |        "currentblocktx": 0,
      |        "difficulty": 1300727.855652248,
      |        "errors": "Warning: unknown new rules activated (versionbit 28)",
      |        "networkhashps": 3269881530063.402,
      |        "pooledtx": 6,
      |        "testnet": true,
      |        "chain": "test"
      |    },
      |    "error": null,
      |    "id": null
      |}
    """.stripMargin.parseJson

  val memPoolInfoResponse =
    """
      |{
      |    "result": {
      |        "size": 4,
      |        "bytes": 1343,
      |        "usage": 5312,
      |        "maxmempool": 300000000,
      |        "mempoolminfee": 0
      |    },
      |    "error": null,
      |    "id": null
      |}
    """.stripMargin.parseJson

  val blockchainInfoResponse =
    """
      |{
      |    "result": {
      |        "chain": "test",
      |        "blocks": 1089634,
      |        "headers": 1089634,
      |        "bestblockhash": "0000000000000732220dd6a674c12f91e2b9cd21817338708f6de5b56f75a7d3",
      |        "difficulty": 1300727.855652248,
      |        "mediantime": 1487591936,
      |        "verificationprogress": 0.999999957737826,
      |        "chainwork": "00000000000000000000000000000000000000000000001f12183d2453fbca7d",
      |        "pruned": false,
      |        "softforks": [
      |            {
      |                "id": "bip34",
      |                "version": 2,
      |                "enforce": {
      |                    "status": true,
      |                    "found": 100,
      |                    "required": 51,
      |                    "window": 100
      |                },
      |                "reject": {
      |                    "status": true,
      |                    "found": 100,
      |                    "required": 75,
      |                    "window": 100
      |                }
      |            },
      |            {
      |                "id": "bip66",
      |                "version": 3,
      |                "enforce": {
      |                    "status": true,
      |                    "found": 100,
      |                    "required": 51,
      |                    "window": 100
      |                },
      |                "reject": {
      |                    "status": true,
      |                    "found": 100,
      |                    "required": 75,
      |                    "window": 100
      |                }
      |            },
      |            {
      |                "id": "bip65",
      |                "version": 4,
      |                "enforce": {
      |                    "status": true,
      |                    "found": 100,
      |                    "required": 51,
      |                    "window": 100
      |                },
      |                "reject": {
      |                    "status": true,
      |                    "found": 100,
      |                    "required": 75,
      |                    "window": 100
      |                }
      |            }
      |        ],
      |        "bip9_softforks": {
      |            "csv": {
      |                "status": "active",
      |                "startTime": 1456790400,
      |                "timeout": 1493596800
      |            },
      |            "segwit": {
      |                "status": "active",
      |                "startTime": 1462060800,
      |                "timeout": 1493596800
      |            }
      |        }
      |    },
      |    "error": null,
      |    "id": null
      |}
    """.stripMargin.parseJson

  val estimateFeeResponse =
    """
      |{
      |    "result": 0.00010244,
      |    "error": null,
      |    "id": null
      |}
    """.stripMargin.parseJson

  val listUnspentResponse =
    """
      |{
      |    "result": [
      |        {
      |            "txid": "b13eae2ff0f833321cfb58fd69724b0388160208fc38a8879551afab06f39900",
      |            "vout": 1,
      |            "address": "mxC1MksGZQAARADNQutrT5FPVn76bqmgZW",
      |            "account": "account1",
      |            "scriptPubKey": "73a914b6e5b09be4bea8f85b075486e293765d0907ee2f88ac",
      |            "amount": 0.00286345,
      |            "confirmations": 411134,
      |            "spendable": true,
      |            "solvable": true
      |        },
      |        {
      |            "txid": "31fab313cd7dcdd57373e50c662cd7de87df6a1137cbea6493947c1540b33103",
      |            "vout": 0,
      |            "address": "n2LFEBSkiJreLLqnjbTP31TiQd4eBt6S3K",
      |            "account": "account2",
      |            "scriptPubKey": "76a914e4535d337068c27e4cda6384b64e2024694e99d388ac",
      |            "amount": 0.00005477,
      |            "confirmations": 25842,
      |            "spendable": true,
      |            "solvable": true
      |        }
      |    ],
      |    "error": null,
      |    "id": null
      |}
    """.stripMargin.parseJson

  val listAccountsResponse =
    """
      |{
      |    "result": {
      |        "": -0.00025328,
      |        "account1": 10.00041465,
      |        "account2": 0
      |    },
      |    "error": null,
      |    "id": null
      |}
    """.stripMargin.parseJson

  val getNewAddressResponse =
    """
      |{
      |    "result": "mxNSnhqVuxe8MwBD86UpG6j5tXNxY7N9Hk",
      |    "error": null,
      |    "id": null
      |}
    """.stripMargin.parseJson

  val addWitnessAddressResponse =
    """
      |{
      |    "result": "2N9pJLCWbaGbfvgD2vYFL3d7NP6ZmCPf6f8",
      |    "error": null,
      |    "id": null
      |}
    """.stripMargin.parseJson

  val sendFromResponse =
    """
      |{
      |    "result": "148a0b45e69bd1734d011299956f6999d39820c62cf4956bfcb820c70cab1902",
      |    "error": null,
      |    "id": null
      |}
    """.stripMargin.parseJson

  val sendToAddressResponse = sendFromResponse

  val generateResponse: JsValue =
    """{
      |    "result": [
      |      "36252b5852a5921bdfca8701f936b39edeb1f8c39fffe73b0d8437921401f9af",
      |      "5f2956817db1e386759aa5794285977c70596b39ea093b9eab0aa4ba8cd50c06"
      |    ],
      |    "error": null,
      |    "id": null
      |}
      |""".stripMargin.parseJson

  val parseErrorResponse: JsValue =
    """
      |{
      |    "result": null,
      |    "error": {
      |        "code": -32700,
      |        "message": "Parse error"
      |    },
      |    "id": null
      |}
    """.stripMargin.parseJson

  val insufficientFundsResponse: JsValue =
    """
      |{
      |    "result": null,
      |    "error": {
      |        "code": -6,
      |        "message": "Account has insufficient funds"
      |    },
      |    "id": null
      |}
    """.stripMargin.parseJson

  val setTxFeeResponse: JsValue =
    """
      |{
      |    "result": true,
      |    "error": null,
      |    "id": null
      |}
    """.stripMargin.parseJson

  val setTxFeeOutOfRangeResponse: JsValue =
    """
      |{
      |    "result": null,
      |    "error": {
      |        "code": -3,
      |        "message": "Amount out of range"
      |    },
      |    "id": null
      |}
    """.stripMargin.parseJson

  val getTransactionResponse: JsValue =
    """
      |{
      |  "result": {
      |    "amount": 0.00000000,
      |    "fee": -0.10000000,
      |    "confirmations": 20,
      |    "blockhash": "609ab7c6d7e0fa44c78752495a7ab3b35ea89370dcbeed6d7df59250854694ff",
      |    "blockindex": 1,
      |    "blocktime": 1517481250,
      |    "txid": "4528087ee62cc971be2d8dcf6c4b39d5603a0bc66cfb16c6f2448ea52f3cda3c",
      |    "walletconflicts": [
      |    ],
      |    "time": 1517481250,
      |    "timereceived": 1517481250,
      |    "bip125-replaceable": "no",
      |    "comment": "TESTTTT",
      |    "details": [
      |      {
      |        "account": "",
      |        "address": "moh4u3yuX6AwiQLzqXLDaVoe4UCMYj6jf5",
      |        "category": "send",
      |        "amount": -10.00000000,
      |        "label": "",
      |        "vout": 0,
      |        "fee": -0.10000000,
      |        "abandoned": false
      |      },
      |      {
      |        "account": "",
      |        "address": "moh4u3yuX6AwiQLzqXLDaVoe4UCMYj6jf5",
      |        "category": "receive",
      |        "amount": 10.00000000,
      |        "label": "",
      |        "vout": 0
      |      }
      |    ],
      |    "hex": "0200000003dedd8d2db4bae4cabd07d5e85be69a6a33efdb88272fdaf3c23af629d4b22d6e010000006b483045022100cae0f14d721639ff1024237ab08fa5d1f7e6a578934c9ed4b92598570d375e41022029bb765ccc5a88394e50f392fd7715555343bd7d10dba727acddb0f4f0d6efe6012103a43d7232c6948eb4d484df2cc03e142df1cf70f984e81720e295449c8ec67c15feffffff6a97252fb14d3062461aba5be26e71ed40f928ab769aee83c6cb17784655a787000000004847304402204597a9cae99db0234ba2fa57e43d5e3b3b6761c9349341041ef265d43f9352ea02206cbde1493f3ab0441f94bc56180d19af73adda583b0512e5fdb38f83bd2f1f9301feffffff876f05ff738e68e4d278e8b8c3bdb4d69fc032618daa5522e2f7d7c37b14c99f010000006b4830450221008a50af872b153bb812f76a2a94409d232b56bca5fa0439746738311149ac6d55022032439a9bae3400f093d9ae9dcf68e82a65b2522967016f8638ab71467c017f3401210232df4cb744091b19e86f67c0952d4b971cbb7f44b154edba73a5dbe0f720b49efeffffff0200ca9a3b000000001976a91459aaad9aca1ff5b001ce4b1608eadc88e3d2d03e88ac13fa1a00000000001976a914658d809a900982df0216229833ae2d3c0b2350b988ac24060000"
      |  },
      |  "error": null,
      |  "id": null
      |}
    """.stripMargin.parseJson
}
