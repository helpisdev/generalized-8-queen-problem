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
        final TreeMap<Integer, Node> instances = new TreeMap<>();

        for (Node node : parents) {
            final int[] instance = node.getSchemaInstance();
            if (instance != null) {
                instances.put(instance[1] - instance[0], node);
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

            if (this.getProgress() < 40) {
                for (Map.Entry<Integer, Node> pair : instances.entrySet()) {
                    final int[] parentRows = ((NodeImpl) pair.getValue()).getRows();

                    final int startIndex = pair.getValue().getSchemaInstance()[0];
                    final int endIndex = pair.getValue().getSchemaInstance()[1];

                    if (endIndex + 1 - startIndex >= 0) {
                        System.arraycopy(parentRows, startIndex, rows, startIndex, endIndex + 1 - startIndex);
                    }

                    if (endIndex + 1 - startIndex == this.queens - 1) {
                        final Node[] newChildren = new Node[this.queens];

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
                }
            }

            childRepresentationBuilder.delete(0, childRepresentationBuilder.length());
            for (int row : rows) {
                childRepresentationBuilder.append(row).append('-');
            }
            childRepresentationBuilder.deleteCharAt(childRepresentationBuilder.length() - 1);

            children[child] = new NodeImpl(childRepresentationBuilder.toString(), this);
        }

        return children;
    }

    @Override
    public Node mutate(Node node) {
        Node mutatedNode = node;

        final int num1 = new Random().nextInt(1000);
        final int num2 = new Random().nextInt(1000);
        final int num3 = new Random().nextInt(1000);

        if (num3 >= num1 && num3 <= num2) {
            final StringBuilder childRepresentationBuilder = new StringBuilder();
            final int[] rows = ((NodeImpl) node).getRows();

            final int mutationIndex = new Random().nextInt(this.queens);
            final int mutation = new Random().nextInt(this.queens);
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
        final int limit = new Random().nextInt(Math.min(this.queens - 3, (super.getPopulation().size() / 4) + 1)) + 2;
        int length = super.getPopulation().size();
        while (length > limit) {

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
        double previousProgress = 0;

        do {
            final Node[] parents = this.pickParents(
                    new Random().nextInt(Math.min(this.queens - 3, (super.getPopulation().size() / 5) + 1)) + 2);
            final Node[] children = this.reproduce(parents);

            for (int child = 0; child < children.length; ++child) {
                children[child] = this.mutate(children[child]);
                ++totalChildren;
            }

            goalNode = (NodeImpl) super.findGoal();
            if (goalNode != null) {
                foundGoal = true;
            }

            previousProgress = this.displayProgress(previousProgress, start, parents.length);

            for (Node child : children) {
                super.addNode(child);
            }

            super.setWeightedPopulation();
            this.decreasePopulation();
        } while (!foundGoal);

        final long end = System.currentTimeMillis();
        this.displaySolution(start, end, goalNode);
        System.out.println("\nTotal children: " + totalChildren);
    }

    private double displayProgress(double previousProgress, long start, int numOfParents) {
        final double currentProgress = this.getProgress();
        if (currentProgress > previousProgress) {
            previousProgress = currentProgress;
            final long progressTime = System.currentTimeMillis();
            System.out.printf("Completed %f%%, %d/%d   ---   ",
                    currentProgress,
                    super.getLastScore(), this.generateFitnessGoal()
            );
            this.displayElapsedTime(start, progressTime);
            System.out.printf("   ---   Current population: %d   ---   Number of parents: %d%n",
                    super.getPopulation().size(),
                    numOfParents
            );
        }

        return previousProgress;
    }

    private double getProgress() {
        return ((double) super.getLastScore() / this.generateFitnessGoal()) * 100;
    }

    private void displaySolution(long start, long end, NodeImpl goalNode) {
        System.out.println(goalNode);
        this.displayElapsedTime(start, end);
    }

    private void displayElapsedTime(long start, long end) {
        final long elapsedTime = end - start;
        java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###.###");
        final double seconds = (double)elapsedTime / 1000;
        if (seconds < 60) {
            System.out.printf("Elapsed time (in seconds): %s\"", formatter.format(seconds));
        } else {
            formatter = new java.text.DecimalFormat("#,###");
            final int minutes = (int) ((double)elapsedTime / 1000 / 60);
            final double remainingSeconds = seconds - minutes * 60;
            System.out.printf("Elapsed time (in minutes and seconds): %d'%s\"", minutes, formatter.format(remainingSeconds));
        }
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
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                isInputCorrect = false;
            }
        } while (!isInputCorrect);
    }
}
