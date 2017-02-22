# play-cinnamon-test
A small test project on Lightbend Cinnamon aka. [Lightbend Monitoring](https://developer.lightbend.com/docs/monitoring/latest/home.html).

## Usage
You should start play with the command `sbt start` which will start play in production mode.

## JMX monitoring
You should launch VisualVM (jvisualvm) and connect to the running play process id, then select the MBeans tab and select the 'metrics' node. You should see metrics from the actor system, including the 'MyActor' actor.