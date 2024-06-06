Java API to receive HTTPS traffic delivered via Load Balancer, DNS with Geographical Load Balancing (GSLB - Global Server Load Balancing)

## Keystore Configuration (SSL Certificates)

### You need to generate a keystore to configure HTTPS. Use the command below to generate a PKCS12 keystore:
keytool -genkeypair -alias myalias -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 3650

### Grafana Configuration
In Grafana, add Prometheus as a data source and set up dashboards to visualize collected metrics.
