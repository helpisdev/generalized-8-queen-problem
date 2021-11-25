package gr.ilias.kapareliotis.geneticAlgorithmInterface;

import java.util.*;

abstract public class GeneticAlgorithm {
    private final LinkedList<Node> population = new LinkedList<>();
    private final HashSet<Node> populationSet = new HashSet<>();
    private final Node[] weightedPopulation = new Node[100];
    private int fitnessGoal;
    private int lastScore = 0;

    public abstract int generateFitnessScore(Node node);

    public abstract int generateFitnessGoal();

    public abstract Node[] reproduce(Node[] parents);

    public abstract Node mutate(Node node);

    public abstract void decreasePopulation();

    public abstract void run();

    public Node[] pickParents(int numOfParents) {
        final Node[] parents = new Node[numOfParents];
        for (int i = 0; i < numOfParents; ++i) {
            final int parentIndex = new Random().nextInt(this.weightedPopulation.length);
            parents[i] = this.weightedPopulation[parentIndex];
        }

        return parents;
    }

    public LinkedList<Node> getPopulation() {
        return this.population;
    }

    public void addNode(Node node) {
        if (this.populationSet.contains(node)) return;
        this.populationSet.add(node);
        this.population.add(node);
    }

    public void removeNode(int index) {
        final Node removedNode = this.population.remove(index);
        this.populationSet.remove(removedNode);
    }

    public Node getNode(int index) {
        return this.population.get(index);
    }

    public void setFitnessGoal() {
        this.fitnessGoal = this.generateFitnessGoal();
    }

    public Node findGoal() {
        for (Node node : this.population) {
            if (this.lastScore < node.getFitnessScore()) {
                this.lastScore = node.getFitnessScore();
            }

            if (node.getFitnessScore() == this.fitnessGoal) {
                return node;
            }
        }
        return null;
    }

    private void setPopulationWeights() {
        int totalScore = 0;
        for (Node node : this.population) {
            totalScore += node.getFitnessScore();
        }
        for (Node node : this.population) {
            node.setWeight((node.getFitnessScore() * this.population.size()) / totalScore);
        }
    }

    public void setWeightedPopulation() {
        this.setPopulationWeights();

        int count = 0;
        for (Node node : this.population) {
            for (int i = count; i < node.getWeight(); ++i, ++count) {
                if (count == this.weightedPopulation.length) break;
                this.weightedPopulation[count] = node;
            }
        }
        if (count < this.weightedPopulation.length) {
            final int random = new Random().nextInt(this.population.size());
            for (int i = count; i < this.weightedPopulation.length; ++i) {
                this.weightedPopulation[i] = this.population.get(random);
            }
        }
        Collections.shuffle(Arrays.asList(this.weightedPopulation));
    }

    public int getLastScore() {
        return this.lastScore;
    }
}
