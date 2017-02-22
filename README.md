# play-cinnamon-test
A small test project on Lightbend Cinnamon aka. [Lightbend Monitoring](https://developer.lightbend.com/docs/monitoring/latest/home.html).

## Introduction
[Lightbend Monitoring](https://developer.lightbend.com/docs/monitoring/latest/home.html) is a suite of insight tools that provides a view into the workings of our distributed platforms. This view allows developers and operations to respond quickly to problems, track down unexpected behavior and even tune your system. As a result, you can deploy your applications to production with confidence.

## Monitoring Play
Although Lightbend Monitoring currently __does not__ [monitor Play applications](https://developer.lightbend.com/docs/monitoring/latest/getting-started/play.html) themselves you may want to set up monitoring in your Play application to monitor any __Akka__ related tasks that happens in the context of the Play application. 

When developing Play applications it is very convenient to use Play’s development run command. However, because of the way the run command is implemented it is not possible to use Lightbend Monitoring in combination with this command. Instead you have to build the Play application as a distribution and run that.

You should start play with the command `sbt start` or `sbt testProd` to enable monitoring.

## JMX monitoring
You should launch VisualVM (jvisualvm) and connect to the running play process id, then select the MBeans tab and select the 'metrics' node. You should see metrics from the actor system, including the 'MyActor' actor.

## Resources
- [Lightbend Monitoring](https://developer.lightbend.com/docs/monitoring/latest/home.html)

## Video
- [(0'56 hr) Monitoring Microservices in Production with Lightbend Platform -  Duncan DeVore and Henrik Engström](https://www.youtube.com/watch?v=UM_lCvyk7rE)
- [(0'40 hr) Monitoring Reactive Applications - by Duncan DeVore & Henrik Engström](https://www.youtube.com/watch?v=eS5KkK5agvk)
- [(1'00 hr) Typesafe Monitoring Webinar - Henrik Engström](https://www.youtube.com/watch?v=5MQR_dMirwc)
- [(0'56 hr) Typesafe Reactive Platform: Monitoring 1.0, Commercial features and more - Jamie Allen](https://www.youtube.com/watch?v=JEPHSdHHWnE)