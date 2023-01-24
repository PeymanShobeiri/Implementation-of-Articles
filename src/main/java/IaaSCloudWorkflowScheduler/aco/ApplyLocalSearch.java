/*
package IaaSCloudWorkflowScheduler.aco;


import java.util.ArrayList;
import java.util.List;

import IaaSCloudWorkflowScheduler.Resource;
import isula.aco.Ant;
import isula.aco.AntPolicy;
import isula.aco.AntPolicyType;
import isula.aco.ConfigurationProvider;

public class ApplyLocalSearch extends AntPolicy<CloudAcoProblemNode, CloudAcoEnvironment> {

	public ApplyLocalSearch() {
		super(AntPolicyType.AFTER_SOLUTION_IS_READY);
	}

	@Override
	public boolean applyPolicy(CloudAcoEnvironment environment, ConfigurationProvider configurationProvider) {
		
		Ant antForWorkflow =getAnt();
		double makespan = getAnt().getSolutionCost(environment);
		Resource [] currentResource;
		for (CloudAcoProblemNode x : getAnt().getSolution()) {
			
		}
		CloudAcoProblemNode[] currentSolution = getAnt().getSolution();
		CloudAcoProblemNode[] localSolutionJobs = currentSolution;
		
		List <Resource> jobsList = new ArrayList<Resource>();
		
		for (CloudAcoProblemNode job : currentSolution) {
			jobsList.add(job.getResource());
		}
		
		List<Resource> localSolution = jobsList;
		
		int indexI = 0;
        boolean lessMakespan = true;

        while (indexI < (currentSolution.length) && lessMakespan) {
        	Resource jobI = localSolution.get(indexI);
            localSolution.remove(indexI);
            int indexJ = 0;

            while (indexJ < currentSolution.length && lessMakespan) {
                localSolution.add(indexJ, jobI);

                Resource[] intermediateSolution = new Resource[currentSolution.length];
                int anotherIndex = 0;

                for (Resource sol : localSolution) {
                    intermediateSolution[anotherIndex] = sol;
                    anotherIndex++;
                }
                CloudAcoProblemNode []intermediateCloudAcoProblemNode = currentSolution;
                int ind =0 ;
                for (Resource x : intermediateSolution ) {
                	intermediateCloudAcoProblemNode[ind].setResource(x);
                	ind++;
                }

                double newMakespan = AntForWorkflow.getScheduleCostAndMakespan(intermediateCloudAcoProblemNode, environment.getProblemGraph());

                if (newMakespan < makespan) {
                    makespan = newMakespan;
                    lessMakespan = false;
                } else {
                    localSolution.remove(indexJ);
                }

                indexJ++;
            }

            if (lessMakespan) {
                localSolution.add(indexI, jobI);
            }
            indexI++;
        }

        int index = 0;
        for (Resource job : localSolution) {
            localSolutionJobs[index].setResource(job); 
            index++;
        }
        getAnt().setSolution(localSolutionJobs);

        return true;
	}

}
*/
