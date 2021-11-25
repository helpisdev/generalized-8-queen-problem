package gr.ilias.kapareliotis.generalized8queenImpl;

import gr.ilias.kapareliotis.geneticAlgorithmInterface.Node;

public class NodeImpl extends Node {
    private final int[] descendingDiagonals;
    private final int[] ascendingDiagonals;
    private final int[] rows;
    private final GeneticAlgorithmImpl algorithm;

    public NodeImpl(String representation, GeneticAlgorithmImpl algorithm) {
        super(representation);
        this.rows = this.generateRows();
        this.descendingDiagonals = this.generateDescendingDiagonals();
        this.ascendingDiagonals = this.generateAscendingDiagonals();
        this.algorithm = algorithm;
        super.setSchemaInstance(this.generateSchemaInstance());
        super.setFitnessScore(this.algorithm.generateFitnessScore(this));
    }

    private int[] generateRows() {
        final String[] rowStrings = super.getRepresentation().split("-");
        final int[] rowNumbers = new int[rowStrings.length];
        for (int i = 0; i < rowNumbers.length; ++i) {
            rowNumbers[i] = Integer.parseInt(rowStrings[i]);
        }
        return rowNumbers;
    }

    private int[] generateDescendingDiagonals() {
        final int[] diagonalNumbers = new int[this.rows.length];
        for (int i = 0; i < diagonalNumbers.length; ++i) {
            diagonalNumbers[i] = this.rows[i] - i;
        }
        return diagonalNumbers;
    }

    private int[] generateAscendingDiagonals() {
        final int[] diagonalNumbers = new int[this.rows.length];
        for (int i = 0; i < diagonalNumbers.length; ++i) {
            diagonalNumbers[i] = this.rows[i] + i;
        }
        return diagonalNumbers;
    }

    public int[] getDescendingDiagonals() {
        return this.descendingDiagonals;
    }
    public int[] getAscendingDiagonals() {
        return this.ascendingDiagonals;
    }

    public int[] getRows() {
        return this.rows;
    }

    @Override
    public int[] generateSchemaInstance() {
        int[] instance = null;
        int firstIndex = 0;
        int lastIndex = 0;
        for (int i = 0; i < this.rows.length; ++i) {
            int tempIndex = i;
            for (int j = i + 1; j < this.rows.length; ++j) {
                if (this.algorithm.generateFitnessGoal(j - i + 1)
                        == this.algorithm.generateFitnessScore(this, i, j))
                {
                    tempIndex = j;
                } else {
                    break;
                }
            }
            if (tempIndex - i > lastIndex - firstIndex) {
                firstIndex = i;
                lastIndex = tempIndex;
            }
            if (lastIndex > i) {
                i = lastIndex;
            }
        }

        if (lastIndex - firstIndex > 0) {
            instance = new int[]{firstIndex, lastIndex};
        }

        return instance;
    }
}
