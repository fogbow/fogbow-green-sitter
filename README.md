##What is the Fogbow Green Sitter and why is it necessary?

The Fogbow Green Sitter is a tool that aims on helping Fogbow users saving energy by consolidating applications in active hosts and by shutting down idle hosts with no instances. In situations where hosts live in an energy-constrained environment,  it is important that, we implement an automatic policy that turns off hosts after instances created by the cloud middleware (such as Openstack and Open Nebula) in the private cloud infrastructure are finished and no user is using the host (in case of an opportunistic deployment). 

The Fogbow Green Sitter will most probably be released in late april or early may.

##How the Green Sitter works

In order to decide which host should be powered on/off, it is necessary to get some information from the cloud, such as how many hosts are in the system and how much CPU is available on each host. A **cloud informartion plugin** is responsible to return a list containing those and other information about each host. The Fogbow Green Sitter was designed to be agnostic to the underlying cloud technology, in that sense, every technology should its own cloud information plugin implementation. At the moment, there is a single plugin implementation for OpenStack. 

Furthermore, several criteria for deciding when a host should be powered on/off can be implemented, since our decision maker core is also a plugin. The Green Sitter comes with a default implementation that works for most of the situations that Fogbow is deployed. 

The default Green Strategy considers that a host is idle when there is no instances, and there is no cloud computing agent enabled and running in the host, what means that neither the private cloud nor a human is using that host. Then the Sitter waits for a grace period before suspending the host, so that we don’t shutdown and turn off computers in such a high frequency that could increase the energy bill instead of lowering its costs due to the powering on/off energy overhead.

The Green Sitter will also wake up sleeping hosts, if there is any with the minimum requirements that fits an incoming Fogbow request. It will always wake the host with the largest capacity (considering CPU and RAM), since the most powerful host will potentially be able to create more VMs.

##Architecture

There are two main parts of the Fogbow Green Sitter: the Agent and the Server. The Agent is the simplest one, it must be installed on each host of the system and will send to the Server the IP of the computer that it is installed and will also receive commands for turning off the computer. 

The Server is a little bit more complex. There are three main parts of the Server system: The Cloud Information Plugin, already described; ; the Green Strategy, which is the heart of Green Sitter and where decisions are made (who is going to be turned up/down),.;and the Communication Component, that provides communication with Agents and with the Fogbow Manager. It receives the information sent by the agent and sends turn on/down signals to it when necessary by using both XMPP and wakeOnLan protocols. It also receives requests from the Fogbow Manager when there is no resources available in the private cloud, so it powers up a computer when possible.

The following image illustrates the architecture described:
<center>![General architecture](http://www.fogbowcloud.org/images/greenSitter.png)</center>