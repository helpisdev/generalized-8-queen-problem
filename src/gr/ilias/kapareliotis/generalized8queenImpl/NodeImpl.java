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
    public int generateSchemaInstance() {
        int index = 0;
        for (int i = 0; i < this.rows.length; ++i) {
            if (this.algorithm.generateFitnessGoal(i + 1)
                    == this.algorithm.generateFitnessScore(this, 0, i))
            {
                ++index;
            }
        }

//        if (index == 9) {
//            System.out.println(this);
//        }

        return index;
    }
}
