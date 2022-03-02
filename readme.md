
# Test SSL Connection


## Usage

```bash
java -jar SSLPoke.jar com.moers.ssl.SSLPoke moers.ch 443
```

## Usage with Proxy

```bash
java -jar SSLPoke.jar -Dhttps.proxyHost=proxy-out -Dhttps.proxyPort=13128  moers.ch 443
```

## Usage with specific keystore

```bash
java  -jar SSLPoke.jar -Djavax.net.ssl.trustStore=/path/to/store/LdapSSLKeyStore.jks -Djavax.net.ssl.trustStorePassword=123 -Djavax.net.ssl.trustStoreType=jks myserver.local 443
```

## import certificate into your local TrustStore

```bash
keytool -import -trustcacerts -storepass changeit -file "./rootCa.cer" -alias C1_ROOT_CA -keystore ./LocalTrustStore

java -Djavax.net.ssl.trustStore=./LocalTrustStore -jar SSLPoke.jar $HOST $PORT
```

## Build

```bash
mvn package
```

## Resource

https://gist.github.com/4ndrej/4547029
https://gist.github.com/bric3/4ac8d5184fdc80c869c70444e591d3de

