
export const data = {
  name: "Subodh",
  age: 23,
};

export const emptyCSV = [];

export const emptyCSVWithHeaders = [["name", "age"]];

export const exampleCSVWithoutHeaders = [
  ["Jay", "21"],
  ["Blake", "20"],
];

export const exampleCSVWithHeaders = [
  ["name", "age"],
  ["Jay", "21"],
  ["Blake", "20"],
];

export const exampleCSVWithHeadersWithHole = [
  ["name", "age"],
  ["Jay", "21"],
  ["Blake", ""],
  ["Jay", "57"]
];

export const exampleCSVWithoutHeadersWithHole = [
  ["Jay", "21"],
  ["Blake", ""],
  ["Jay", "57"]
];

export const oneRow = [
  ["Jay", "21"],
];

export const oneCol = [
  ["Jay"],
  ["Blake"],
  ["Jay"]
];



export const filepathToParsedCSVMap = [
  ["test_with_headers", exampleCSVWithHeaders],
  ["test_without_headers", exampleCSVWithoutHeaders],
  ["test_empty", emptyCSV],
  ["test_empty_with_headers", emptyCSVWithHeaders],
  ["test_with_headers_with_hole", exampleCSVWithHeadersWithHole],
  ["test_without_headers_with_hole", exampleCSVWithoutHeadersWithHole]]

export const parsedCSVToFilepathMap = [
  [exampleCSVWithHeaders, "test_with_headers"],
  [exampleCSVWithoutHeaders, "test_without_headers"],
  [emptyCSV, "test_empty"],
  [emptyCSVWithHeaders, "test_empty_with_headers"],
  [exampleCSVWithHeadersWithHole, "test_with_headers_with_hole"],
  [exampleCSVWithoutHeadersWithHole, "test_without_headers_with_hole"]]

// Commented out returning blank result to be consistent with printing informative message in search function.

export const searchParamsToSearchResultsMap1 = new Map<string[], string[][]>([
  [["0", "Jay"], [["Jay", "21"]]],
  [["0", "Blake"], [["Blake", "20"]]],
  // [["0", "21"], []],
  // [["0", "20"], []],
  // [["1", "Jay"], []],
  // [["1", "Blake"], []],
  [["1", "21"], [["Jay", "21"]]],
  [["1", "20"], [["Blake", "20"]]],
  [["name", "Jay"], [["Jay", "21"]]],
  [["name", "Blake"], [["Blake", "20"]]],
  // [["name", "20"], [[]]],
  // [["name", "21"], [[]]],
  [["age", "21"], [["Jay", "21"]]],
  [["age", "20"], [["Blake", "20"]]],
  // [["age", "Jay"], [[]]],
  // [["age", "Blake"], [[]]],
]);

export const searchParamsToSearchResultsMap2 = new Map<string[], string[][]>([
  [["0", "Jay"], [["Jay", "21"], ["Jay", "57"]]],
  [["0", "Blake"], [["Blake", ""]]],
  // [["0", "21"], []],
  // [["0", "20"], []],
  // [["1", "Jay"], []],
  // [["1", "Blake"], []],
  [["1", "21"], [["Jay", "21"]]],
  [["1", "20"], [["Blake", ""]]],
  [["1", "57"], [["Jay", "57"]]],
  [["name", "Jay"], [["Jay", "21"], ["Jay", "57"]]],
  [["name", "Blake"], [["Blake", ""]]],
  // [["name", "20"], [[]]],
  // [["name", "21"], [[]]],
  [["age", "21"], [["Jay", "21"]]],
  [["age", "57"], [["Jay", "57"]]],
  [["age", "20"], [["Blake", ""]]],
  // [["age", "Jay"], [[]]],
  // [["age", "Blake"], [[]]],
]);

export const filepathToSearchResultsMap = new Map<
  string,
  Map<string[], string[][]>
>([["test_with_headers", searchParamsToSearchResultsMap1],
["test_with_headers_with_hole", searchParamsToSearchResultsMap2]]);

