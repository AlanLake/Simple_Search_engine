package search;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

interface FindAPersonMethod {

    void find(String searchQuery, ArrayList<String> data, Map<String, ArrayList<Integer>>  invertedIndex);

}

class All implements FindAPersonMethod {

    @Override
    public void find(String searchQuery, ArrayList<String> data, Map<String, ArrayList<Integer>> invertedIndex) {
        Set<Integer> resultLines = new HashSet<>(0);
        String[] queryWords = searchQuery.split("\\s+");
        int iterator = 0;
        do {
            if (invertedIndex.containsKey(queryWords[iterator].toLowerCase())) {
                resultLines.addAll(invertedIndex.get(queryWords[iterator].toLowerCase()));
            }
            iterator++;
        } while (resultLines.size() == 0 && iterator < queryWords.length);
        if (iterator < queryWords.length) {
            Set<Integer> tempSet = new HashSet<>(0);
            for (int i = iterator; i < queryWords.length; i++) {
                if (invertedIndex.containsKey(queryWords[i].toLowerCase())) {
                    tempSet.addAll(invertedIndex.get(queryWords[i].toLowerCase()));
                    resultLines.retainAll(tempSet);
                }
                tempSet.clear();
            }
            System.out.printf("%d persons found:\n", resultLines.size());
            resultLines.forEach(numberLine -> System.out.println(data.get(numberLine)));
        } else {
            System.out.println("No matching people found.");
        }
    }
}

class Any implements FindAPersonMethod {

    @Override
    public void find(String searchQuery, ArrayList<String> data, Map<String, ArrayList<Integer>> invertedIndex) {
        Set<Integer> resultLines = new HashSet<>(0);
        for (String queryWord : searchQuery.split("\\s+")) {
            if (invertedIndex.containsKey(queryWord.toLowerCase())) {
                resultLines.addAll(invertedIndex.get(queryWord.toLowerCase()));
            }
        }
        if (resultLines.size() > 0) {
            System.out.printf("%d persons found:\n", resultLines.size());
            resultLines.forEach(numberLine -> System.out.println(data.get(numberLine)));
        } else {
            System.out.println("No matching people found.");
        }
    }
}

class None implements FindAPersonMethod {

    @Override
    public void find(String searchQuery, ArrayList<String> data, Map<String, ArrayList<Integer>> invertedIndex) {
        Set<Integer> resultLines = new HashSet<>(0);
        for (String queryWord : searchQuery.split("\\s+")) {
            if (invertedIndex.containsKey(queryWord.toLowerCase())) {
                resultLines.addAll(invertedIndex.get(queryWord.toLowerCase()));
            }
        }
        Set<Integer> allLines = new HashSet<>(0);
        for (ArrayList<Integer> list : invertedIndex.values()) {
            allLines.addAll(list);
        }
        Set<Integer> result = new HashSet<>(allLines);
        result.removeAll(resultLines);
        resultLines = result;
        if (resultLines.size() > 0) {
            System.out.printf("%d persons found:\n", resultLines.size());
            resultLines.forEach(numberLine -> System.out.println(data.get(numberLine)));
        } else {
            System.out.println("No matching people found.");
        }
    }
}

class Finder {

    private FindAPersonMethod method;

    public void setMethod(FindAPersonMethod method) {
        this.method = method;
    }

    public void find(String searchQuery, ArrayList<String> data, Map<String, ArrayList<Integer>> invertedIndex) {
        this.method.find(searchQuery, data, invertedIndex);
    }
}


public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        String path = args[1];
        Scanner linesScan = new Scanner(new File(path));
        ArrayList<String> data = new ArrayList<>();
        while (linesScan.hasNextLine()) {
            data.add(linesScan.nextLine());
        }
        linesScan.close();

        Map<String, ArrayList<Integer>> invertedIndex = new HashMap<>();
        int iterator = 0;
        for (String line : data) {
            for (String word : line.split("\\s+")) {
                if (invertedIndex.keySet().contains(word.toLowerCase())) {
                    ArrayList<Integer> temp = invertedIndex.get(word.toLowerCase());
                    temp.add(Integer.valueOf(iterator));
                } else {
                    ArrayList<Integer> temp = new ArrayList<>();
                    temp.add(Integer.valueOf(iterator));
                    invertedIndex.put(word.toLowerCase(), temp);
                }
            }
            iterator++;
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Menu ===\n" +
                    "1. Find a person\n" +
                    "2. Print all people\n" +
                    "0. Exit");

            switch (Integer.parseInt(scanner.nextLine().trim())) {
                case 1:
                    System.out.println("\nSelect a matching strategy: ALL, ANY, NONE");
                    String matchingStrategy = scanner.nextLine();
                    if ("ALLANYNONE".contains(matchingStrategy)) {
                        Finder finder = new Finder();
                        switch (matchingStrategy) {
                            case ("ALL"):
                                finder.setMethod(new All());
                                break;
                            case ("ANY"):
                                finder.setMethod(new Any());
                                break;
                            case ("NONE"):
                                finder.setMethod(new None());
                                break;
                        }
                        System.out.println("\nEnter a name or email to search all suitable people.");
                        String searchQuery = scanner.nextLine().trim();
                        System.out.println();
                        if (searchQuery.length() > 0) {
                            finder.find(searchQuery, data, invertedIndex);
                        } else {
                            System.out.println("No matching people found.");
                        }
                    } else {
                        System.out.println("Incorrect option! Try again.");
                    }
                    break;
                case 2:
                    printAllPeople(data);
                    break;
                case 0:
                    System.out.println("\nBye!");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Incorrect option! Try again.");
                    break;
            }
        }
    }

    static void printAllPeople(ArrayList<String> data) {
        System.out.println("\n=== List of people ===");
        data.forEach(System.out::println);
    }
}