// see: https://developer.lightbend.com/docs/monitoring
addSbtPlugin("com.lightbend.cinnamon" % "sbt-cinnamon" % "2.2.3")

// to create your '.credentials' file: https://developer.lightbend.com/docs/reactive-platform/2.0/setup/setup-sbt.html
// for credentials: https://www.lightbend.com/product/lightbend-reactive-platform/credentials
// create a developer account: https://www.lightbend.com/account
credentials += Credentials(Path.userHome / ".lightbend" / "commercial.credentials")

resolvers += Resolver.url("lightbend-commercial", url("https://repo.lightbend.com/commercial-releases"))(Resolver.ivyStylePatterns)

/**
 * Create a file in ~/.lightbend/commercial.credentials
 *
 * Add the following lines
 *
 * realm = Bintray
 * host = dl.bintray.com
 * user = <your very long userid@lightbend here>
 * password = <your very long password here>
 */