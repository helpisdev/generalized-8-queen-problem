package gr.ilias.kapareliotis.geneticAlgorithmInterface;

import java.util.Objects;

abstract public class Node {
    private final String representation;
    private int[] schemaInstance = new int[2];
    private int fitnessScore;
    private int weight;

    public Node(String representation) {
        this.representation = representation;
    }

    public String getRepresentation() {
        return this.representation;
    }

    public int getFitnessScore() {
        return this.fitnessScore;
    }

    public void setFitnessScore(int fitnessScore) {
        this.fitnessScore = fitnessScore;
    }

    public int getWeight() {
        return this.weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setSchemaInstance(int[] schema) {
        this.schemaInstance = schema;
    }

    public int[] getSchemaInstance() {
        return this.schemaInstance;
    }

    public abstract int[] generateSchemaInstance();

    @Override
    public String toString() {
        return this.representation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node node)) return false;
        return Objects.equals(this.representation, node.representation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.representation);
    }
}
