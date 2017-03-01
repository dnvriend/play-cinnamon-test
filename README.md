# play-cinnamon-test
A small test project on Lightbend Cinnamon aka. [Lightbend Monitoring](https://developer.lightbend.com/docs/monitoring/latest/home.html).

## Introduction
[Lightbend Monitoring](https://developer.lightbend.com/docs/monitoring/latest/home.html) is a suite of insight tools that provides a view into the workings of our distributed platforms. This view allows developers and operations to respond quickly to problems, track down unexpected behavior and even tune your system. As a result, you can deploy your applications to production with confidence.

## Note
Swagger and Cinnamon don't go well together, 'Caused by: com.fasterxml.jackson.databind.JsonMappingException: Incompatible Jackson version: 2.8.5'.

## Monitoring Play
Although Lightbend Monitoring currently __does not__ [monitor Play applications](https://developer.lightbend.com/docs/monitoring/latest/getting-started/play.html) themselves you may want to set up monitoring in your Play application to monitor any __Akka__ related tasks that happens in the context of the Play application. 

When developing Play applications it is very convenient to use Play’s development run command. However, because of the way the run command is implemented it is not possible to use Lightbend Monitoring in combination with this command. Instead you have to build the Play application as a distribution and run that.

You should start play with the command `sbt start` or `sbt testProd` to enable monitoring.

## JMX monitoring
You should launch VisualVM (jvisualvm) and connect to the running play process id, then select the MBeans tab and select the 'metrics' node. You should see metrics from the actor system, including the 'MyActor' actor.

## Commercial features
To use the following, you'll have to create a
(free) [Lightbend Developer Account](https://www.lightbend.com/account).
You then have to create your '.credentials' file, the necessary credentials be
requested at the [Lightbend Credentials Page](https://www.lightbend.com/product/lightbend-reactive-platform/credentials)
and with that file you'll have so setup your [Lightbend Reactive Platform](https://developer.lightbend.com/docs/reactive-platform/2.0/setup/setup-sbt.html).


Create a file in `~/.lightbend/commercial.credentials`:

```
realm = Bintray
host = dl.bintray.com
user = <your very long userid@lightbend here>
password = <your very long password here>
```

## Available features:

- proxying
- visualization
- logging
- lite-logging
- monitoring

## Configure address aliases
In order to run a ConductR cluster locally, we use network address aliases. These address aliases will allow ConductR
to bind to the required ports to run locally without port collisions. Since we will be starting 3 node cluster,
3 address aliases are required for each node respectively.

The address aliases are temporary. If you reboot, you'll need to run the above commands before running the sandbox again.

For macOS, execute the following commands to create the address aliases:

```bash
sudo sh -c "ifconfig lo0 alias 192.168.10.1 255.255.255.0 && \
ifconfig lo0 alias 192.168.10.2 255.255.255.0 && \
ifconfig lo0 alias 192.168.10.3 255.255.255.0"
```

## Enable monitoring
Run sandbox with monitoring:

```bash
sandbox run 2.0.2 -n 3 --feature visualization --feature monitoring
```

After a while (a minute or so) you should see the following:

```bash
$ conduct info
ID               NAME                     #REP  #STR  #RUN
73595ec          visualizer                  1     0     1
bdfa43d-e5f3504  conductr-haproxy            1     0     1
06d370b          conductr-kibana             1     0     1
d4bdc6c          cinnamon-grafana-docker     1     0     1
85dd265          conductr-elasticsearch      1     0     1
```

## Deploy the application with ConductR
First you'll have to create a bundle. The easiest way is to use sbt for that:

```bash
[play-cinnamon-test] $ bundle:dist
...
[info] Done packaging.
[info] Bundle has been created: /Users/dennis/projects/play-cinnamon-test/target/bundle/play-cinnamon-test-v1-19c248a4ee742e34c644db236acf662ea6ac37e43747df86d5c8059bf28261d2.zip
[success] Total time: 11 s, completed 1-mrt-2017 12:43:35
```

Then ConductR needs to to load the bundle:

```bash
[play-cinnamon-test] $ conduct load /Users/dennis/projects/play-cinnamon-test/target/bundle/play-cinnamon-test-v1-19c248a4ee742e34c644db236acf662ea6ac37e43747df86d5c8059bf28261d2.zip
Retrieving bundle..
Retrieving file:///Users/dennis/projects/play-cinnamon-test/target/bundle/play-cinnamon-test-v1-19c248a4ee742e34c644db236acf662ea6ac37e43747df86d5c8059bf28261d2.zip
Loading bundle to ConductR..
Bundle 19c248a4ee742e34c644db236acf662e is installed
Bundle loaded.
Start bundle with: conduct run 19c248a
Unload bundle with: conduct unload 19c248a
Print ConductR info with: conduct info
```

Next we need to run the bundle:

```bash
[play-cinnamon-test] $ conduct run 19c248a
Bundle run request sent.
Bundle 19c248a4ee742e34c644db236acf662e waiting to reach expected scale 1
Bundle 19c248a4ee742e34c644db236acf662e expected scale 1 is met
Stop bundle with: conduct stop 19c248a
Print ConductR info with: conduct info
[success] Total time: 5 s, completed 1-mrt-2017 12:46:15
```

With a little luck we can see the bundle running in both the console and the [visualizer](http://192.168.10.1:9999):

```bash
[play-cinnamon-test] $ conduct info
ID               NAME                     #REP  #STR  #RUN
19c248a          play-cinnamon-test          1     0     1
73595ec          visualizer                  1     0     1
bdfa43d-e5f3504  conductr-haproxy            1     0     1
06d370b          conductr-kibana             1     0     1
d4bdc6c          cinnamon-grafana-docker     1     0     1
85dd265          conductr-elasticsearch      1     0     1
```

Lets find out the available service-names:

```bash
[play-cinnamon-test] $ conduct service-names
SERVICE NAME    BUNDLE ID  BUNDLE NAME              STATUS
elastic-search  85dd265    conductr-elasticsearch   Running
es-internal     85dd265    conductr-elasticsearch   Running
grafana         d4bdc6c    cinnamon-grafana-docker  Running
kibana          06d370b    conductr-kibana          Running
play            19c248a    play-cinnamon-test       Running
visualizer      73595ec    visualizer               Running
```

Alright, so our application is availabel at 'http://192.168.10.1:9000/play', so our services
are available at 'http://192.168.10.1:9000/play/api/actors' for example:

```bash
$ http 192.168.10.1:9000/play/api/actor
HTTP/1.1 200 OK
Content-Length: 13
Content-Type: text/plain; charset=utf-8
Date: Wed, 01 Mar 2017 11:48:58 GMT

Hello World!!
```

## Grafana


## Explore
As described in the [cinnamon user manual](https://developer.lightbend.com/docs/monitoring/latest/sandbox/explore.html):

- [Proxy port 9000](http://192.168.10.1:9000)
- [ConductR visualizer on port 9999](http://192.168.10.1:9999)
- [Kibana on port 5601](http://192.168.10.1:5601)
- [Grafana on port 3000](http://192.168.10.1:3000)

## Resources
- [Lightbend Monitoring](https://developer.lightbend.com/docs/monitoring/latest/home.html)

## Video
- [(0'56 hr) Monitoring Microservices in Production with Lightbend Platform -  Duncan DeVore and Henrik Engström](https://www.youtube.com/watch?v=UM_lCvyk7rE)
- [(0'40 hr) Monitoring Reactive Applications - by Duncan DeVore & Henrik Engström](https://www.youtube.com/watch?v=eS5KkK5agvk)
- [(1'00 hr) Typesafe Monitoring Webinar - Henrik Engström](https://www.youtube.com/watch?v=5MQR_dMirwc)
- [(0'56 hr) Typesafe Reactive Platform: Monitoring 1.0, Commercial features and more - Jamie Allen](https://www.youtube.com/watch?v=JEPHSdHHWnE)