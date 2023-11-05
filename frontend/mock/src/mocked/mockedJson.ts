// Sample CSV data as 2D arrays of strings
const CSV1: string[][] = [
  ["Name", "Age", "Location"],
  ["Alice", "28", "New York"],
  ["Bob", "32", "Los Angeles"],
  ["Charlie", "24", "San Francisco"],
];

const CSV2: string[][] = [
  ["Product", "Price", "In Stock"],
  ["Laptop", "899", "Yes"],
  ["Smartphone", "499", "No"],
  ["Tablet", "299", "Yes"],
];

const CSV3: string[][] = [
  ["Country", "Capital", "Population"],
  ["USA", "Washington D.C.", "331 million"],
  ["France", "Paris", "67 million"],
  ["Japan", "Tokyo", "126 million"],
];

// Sample CSV data without headers
const CSV4: string[][] = [
  ["Alice", "28", "New York"],
  ["Bob", "32", "Los Angeles"],
  ["Charlie", "24", "San Francisco"],
  ["David", "30", "Chicago"],
];

// Create a map that associates file paths with datasets
export const mockDataMap: Record<string, string[][]> = {
  "path/to/dataset1.csv": CSV1,
  "path/to/dataset2.csv": CSV2,
  "path/to/dataset3.csv": CSV3,
  "path/to/dataset4.csv": CSV4,
};

// Mock search map for CSV1
export const mockSearchMapCSV1: Record<string, string[][]> = {
  "search name bob": [["Bob", "32", "Los Angeles"]],
  "search age 28": [["Alice", "28", "New York"]],
  "search location san francisco": [["Charlie", "24", "San Francisco"]],
  "search name alice": [["Alice", "28", "New York"]],
  "search age 32": [["Bob", "32", "Los Angeles"]],
  "search location los angeles": [["Bob", "32", "Los Angeles"]],
};

// Mock search map for CSV2
export const mockSearchMapCSV2: Record<string, string[][]> = {
  "search product laptop": [["Laptop", "899", "Yes"]],
  "search product smartphone": [["Smartphone", "499", "No"]],
  "search product tablet": [["Tablet", "299", "Yes"]],
  "search product monitor": [["Monitor", "199", "Yes"]],
  "search in-stock yes": [
    ["Laptop", "899", "Yes"],
    ["Tablet", "299", "Yes"],
  ],
  "search in-stock no": [["Smartphone", "499", "No"]],
};

// Mock search map for CSV3
export const mockSearchMapCSV3: Record<string, string[][]> = {
  "search country usa": [["USA", "Washington D.C.", "331 million"]],
  "search country france": [["France", "Paris", "67 million"]],
  "search country japan": [["Japan", "Tokyo", "126 million"]],
  "search capital washington d.c.": [["USA", "Washington D.C.", "331 million"]],
  "search capital paris": [["France", "Paris", "67 million"]],
  "search capital tokyo": [["Japan", "Tokyo", "126 million"]],
};

// Mock search map for CSV4 (searching by column index)
export const mockSearchMapCSV4: Record<string, string[][]> = {
  "search 0 bob": [["Bob", "32", "Los Angeles"]],
  "search 1 28": [["Alice", "28", "New York"]],
  "search 2 san francisco": [["Charlie", "24", "San Francisco"]],
  "search 0 david": [["David", "30", "Chicago"]],
};

// Create a map that associates file paths with corresponding mockSearchMap
export const fileToSearch: Record<string, Record<string, string[][]>> = {
  "path/to/dataset1.csv": mockSearchMapCSV1,
  "path/to/dataset2.csv": mockSearchMapCSV2,
  "path/to/dataset3.csv": mockSearchMapCSV3,
  "path/to/dataset4.csv": mockSearchMapCSV4,
};
