
# Test SSL Connection with Proxy Option

## Usage

```bash
# download the latest version
curl -L -O https://github.com/bmoers/sslpoke/raw/master/SSLPoke.jar

# run
java -jar SSLPoke.jar themoerch.com 443
```

## Usage with Proxy

```bash
java -Dhttps.proxyHost=212.225.137.109 \
     -Dhttps.proxyPort=8080 \
     -jar SSLPoke.jar themoerch.com 443

> com.moers.ssl.SSLPokeProxy
> Using proxy settings: 212.225.137.109:8080
> Response: HTTP/1.1 200 OK
> Successfully connected to themoerch.com:443
```

## Usage with specific keystore

```bash
# basic usage
java -Djavax.net.ssl.trustStore=/path/to/store/cacerts \
     -Dhttps.proxyHost=proxy.host.com \
     -Dhttps.proxyPort=8080 \
     -jar SSLPoke.jar themoerch.com 443

# extended
java -Djavax.net.ssl.trustStore=/path/to/store/LdapSSLKeyStore.jks \
     -Djavax.net.ssl.trustStorePassword=123 \
     -Djavax.net.ssl.trustStoreType=jks \
     -jar SSLPoke.jar themoerch.com 443
```

## Import certificate into your local TrustStore

```bash
# use keytool to import the certificate
keytool -import -trustcacerts -storepass changeit -noprompt -file ./rootCa.cer -alias ROOT_CA -keystore ./LocalTrustStore 

java -Djavax.net.ssl.trustStore=./LocalTrustStore \
     -jar SSLPoke.jar themoerch.com 443
```

## Cert Issues

If you get an `PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target` error this indicates that the Proxy is intercepting the TLS handshake and you require to add its CA to your local TrustStore.

## Build

```bash
mvn package
```

## Resource

https://gist.github.com/4ndrej/4547029  
https://gist.github.com/bric3/4ac8d5184fdc80c869c70444e591d3de
