package pl.ug.airport;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/*
 * DUMMY, nie uzywany agent-demo
 */
public class MasterAgent extends Agent{

	@Override
	protected void setup(){
		//zainicjuj agenta, uruchom jego zachowania
		
		//TODO: czy master jest konieczny? moze sie on stac waskim gardlem. do przegadania
		//master moglby odbierac sygnaly od pozostalych agentow i przekazywac je dalej
		Behaviour proxyRequests = new CyclicBehaviour(this) {
			@Override
			public void action() {
				ACLMessage rec = receive();
				if(rec != null){
					System.out.println("Recived: " + rec.getContent());
				}
			}
		};
		
		this.addBehaviour(proxyRequests);
	}
	
}
