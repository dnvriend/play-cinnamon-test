// Enable the Cinnamon Lightbend Monitoring sbt plugin
enablePlugins (Cinnamon)

// Add the Monitoring Agent for run and test
cinnamon in run := true
cinnamon in test := true

// Use Mapped Diagnostic Context for adding extra identifiers to log messages.
libraryDependencies += Cinnamon.library.cinnamonSlf4jMdc

// Use Coda Hale Metrics
// http://metrics.dropwizard.io/3.1.0/
libraryDependencies += Cinnamon.library.cinnamonCHMetrics
  
// Use Akka instrumentation
libraryDependencies += Cinnamon.library.cinnamonAkka

/**
plugins:
 com.lightbend.cinnamon.sbt.Cinnamon: enabled in play-seed
 com.lightbend.cinnamon.sbt.CinnamonAgentOnly: enabled in play-seed
**/