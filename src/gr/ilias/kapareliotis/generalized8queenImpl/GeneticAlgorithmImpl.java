package gr.ilias.kapareliotis.generalized8queenImpl;

import gr.ilias.kapareliotis.geneticAlgorithmInterface.GeneticAlgorithm;
import gr.ilias.kapareliotis.geneticAlgorithmInterface.Node;

import java.util.*;

public class GeneticAlgorithmImpl extends GeneticAlgorithm {
    private int queens;
    private int threshold = 0;

    public int generateFitnessGoal(int queens) {
        return queens * (queens - 1) / 2;
    }

    public int generateFitnessScore(NodeImpl node, int startIndex, int endIndex) {
        final int[] nodeRows = node.getRows();
        final int[] nodeDescendingDiagonals = node.getDescendingDiagonals();
        final int[] nodeAscendingDiagonals = node.getAscendingDiagonals();
        final int fitnessGoal = this.generateFitnessGoal(endIndex + 1 - startIndex);

        int attackingPairs = 0;

        for (int i = startIndex; i < endIndex; ++i) {
            final int rowFreq =
                    Collections.frequency(
                            Arrays.stream(Arrays.copyOfRange(nodeRows, i, endIndex + 1)).boxed().toList(),
                            nodeRows[i]) - 1;

            final int descendingDiagonalFreq =
                    Collections.frequency(
                            Arrays.stream(Arrays.copyOfRange(nodeDescendingDiagonals, i, endIndex + 1)).boxed().toList(),
                            nodeDescendingDiagonals[i]) - 1;

            final int ascendingDiagonalFreq =
                    Collections.frequency(
                            Arrays.stream(Arrays.copyOfRange(nodeAscendingDiagonals, i, endIndex + 1)).boxed().toList(),
                            nodeAscendingDiagonals[i]) - 1;

            attackingPairs += descendingDiagonalFreq + ascendingDiagonalFreq + rowFreq;
        }

        return fitnessGoal - attackingPairs;
    }

    @Override
    public int generateFitnessGoal() {
        return this.generateFitnessGoal(this.queens);
    }

    @Override
    public int generateFitnessScore(Node node) {
        return this.generateFitnessScore((NodeImpl) node, 0, this.queens - 1);
    }

    @Override
    public Node[] reproduce(Node[] parents) {
        int schema = 0;
        Node schemaNode = null;
        for (Node parent : parents) {
            if (parent.getSchemaInstance() > schema) {
                schema = parent.getSchemaInstance();
                schemaNode = parent;
            }
        }

        final Node[] children = new Node[parents.length];
        final StringBuilder childRepresentationBuilder = new StringBuilder();
        final int[] rows = new int[this.queens];
        final int[] indexes = new int[parents.length + 1];
        Arrays.fill(indexes, -1);
        indexes[0] = 0;
        indexes[parents.length] = this.queens - 1;

        for (int i = 1; i < indexes.length; ++i) {
            while (indexes[i] == -1 || Collections.frequency(Arrays.stream(indexes).boxed().toList(), indexes[i]) > 1) {
                indexes[i] = new Random().nextInt(this.queens - 1);
            }
        }

        Arrays.sort(indexes);

        for (int child = 0; child < children.length; ++child) {
            int parent = child;

            for (int index = 0; index < indexes.length - 1; ++index) {
                final int startIndex = indexes[index];
                final int endIndex = indexes[index + 1];

                final int[] parentRows = ((NodeImpl) parents[parent]).getRows();

                if (endIndex + 1 - startIndex >= 0) {
                    System.arraycopy(parentRows, startIndex, rows, startIndex, endIndex - startIndex);
                }

                ++parent;
                if (parent == parents.length) {
                    parent = 0;
                }
            }

            if (schemaNode != null) {
                for (int row = 0; row <= schema; ++row) {
                    rows[row] = ((NodeImpl) schemaNode).getRows()[row];
                }
            }

            childRepresentationBuilder.delete(0, childRepresentationBuilder.length());
            for (int row : rows) {
                childRepresentationBuilder.append(row).append('-');
            }
            childRepresentationBuilder.deleteCharAt(childRepresentationBuilder.length() - 1);

            children[child] = new NodeImpl(childRepresentationBuilder.toString(), this);
        }

        if (schemaNode != null && schema == this.queens - 2) {
            final Node[] newChildren = new Node[this.queens];
            for (int row = 0; row <= schema; ++row) {
                rows[row] = ((NodeImpl) schemaNode).getRows()[row];
            }
            for (int i = 0; i < this.queens; ++i) {
                rows[this.queens  - 1] = i;
                childRepresentationBuilder.delete(0, childRepresentationBuilder.length());
                for (int row : rows) {
                    childRepresentationBuilder.append(row).append('-');
                }
                childRepresentationBuilder.deleteCharAt(childRepresentationBuilder.length() - 1);

                newChildren[i] = new NodeImpl(childRepresentationBuilder.toString(), this);
            }
            return newChildren;
        }

        return children;
    }

    @Override
    public Node mutate(Node node) {
        Node mutatedNode = node;

        final int num1 = new Random().nextInt(1000);
        final int num2 = new Random().nextInt(1000);
        final int num3 = new Random().nextInt(250);

        if (num3 >= num1 && num3 <= num2) {
            final int mutationIndex = new Random().nextInt(this.queens);
            final int mutation = new Random().nextInt(this.queens);

            final StringBuilder childRepresentationBuilder = new StringBuilder();
            final int[] rows = ((NodeImpl) node).getRows();
            rows[mutationIndex] = mutation;

            for (int row : rows) {
                childRepresentationBuilder.append(row).append('-');
            }
            childRepresentationBuilder.deleteCharAt(childRepresentationBuilder.length() - 1);

            mutatedNode = new NodeImpl(childRepresentationBuilder.toString(), this);
        }

        return mutatedNode;
    }

    @Override
    public void decreasePopulation() {
        while (super.getPopulation().size() > this.threshold / 1.5) {
            int length = super.getPopulation().size();
            for (int i = 0; i < length; ++i) {
                final Node node = super.getNode(i);
                if (node.getFitnessScore() < this.threshold) {
                    --length;
                    super.removeNode(i);
                    --i;
                }
            }
            if (this.threshold == super.getLastScore()) break;
            ++this.threshold;
            System.out.println("Threshold: " + this.threshold + "  --  Goal: " + this.generateFitnessGoal());
        }
    }

    @Override
    public void run() {
        this.getUserInput();
        final long start = System.currentTimeMillis();
        this.generateRandomGridImages(this.queens * 2);
        super.setWeightedPopulation();
        boolean foundGoal = false;
        NodeImpl goalNode;
        long totalChildren = 0;

        do {
            final Node[] parents = this.pickParents(2);
            final Node[] children = this.reproduce(parents);

            for (int child = 0; child < children.length; ++child) {
                children[child] = this.mutate(children[child]);
//                System.out.println("Child: " + children[child]);
                ++totalChildren;
            }

            goalNode = (NodeImpl) super.findGoal();
            if (goalNode != null) {
                foundGoal = true;
            }

            for (Node child : children) {
                super.addNode(child);
            }

            super.setWeightedPopulation();
            this.decreasePopulation();
        } while (!foundGoal);

        final long end = System.currentTimeMillis();
        this.displaySolution(start, end, goalNode);
        System.out.println("Total children: " + totalChildren);
    }

    private void displaySolution(long start, long end, NodeImpl goalNode) {
        final long elapsedTime = end - start;
        java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###.###");
        System.out.println(goalNode);
        System.out.println("Elapsed time (in seconds): " + formatter.format((double)elapsedTime / 1000));
    }

    private void generateRandomGridImages(int num) {
        for (int i = 0; i < num; ++i) {
            final StringBuilder imageBuilder = new StringBuilder();
            for (int row = 0; row < this.queens - 1; ++row) {
                imageBuilder.append(new Random().nextInt(this.queens)).append('-');
            }
            imageBuilder.append(new Random().nextInt(this.queens));

            NodeImpl node = new NodeImpl(imageBuilder.toString(), this);
            super.addNode(node);
        }
    }

    private void getUserInput() {
        final Scanner scanner = new Scanner(System.in);
        boolean isInputCorrect;
        String firstGridImage;

        do {
            try {
                isInputCorrect = true;

                System.out.print("Enter the number of queens (>3): ");
                this.queens = scanner.nextInt();
                if (this.queens < 3) {
                    System.out.println("The number of queens must be greater than 3.");
                    isInputCorrect = false;
                    continue;
                }

                super.setFitnessGoal();

                System.out.printf(
                        "Enter the starting grid of the queens.%n%s %d columns in the form n-n-n-n: ",
                        "Enter the row number the queen is currently at for the n-th column for",
                        this.queens
                );
                firstGridImage = scanner.next();

                if (!firstGridImage.matches("(([\\d]+[-?])+)[\\d]+")
                        || firstGridImage.split("-").length != this.queens) {
                    System.out.println("The format is incorrect.");
                    isInputCorrect = false;
                    continue;
                }

                NodeImpl root = new NodeImpl(firstGridImage, this);
                final int[] rows = root.getRows();
                for (int row : rows) {
                    if (row >= this.queens) {
                        System.out.printf("For %d queens the column numbers must be from 0 to %d.",
                                this.queens, this.queens - 1);
                        isInputCorrect = false;
                    }
                }

                if (isInputCorrect) {
                    super.addNode(root);
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                isInputCorrect = false;
            }
        } while (!isInputCorrect);
    }
}
