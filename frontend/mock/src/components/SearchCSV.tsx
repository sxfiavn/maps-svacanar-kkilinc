import React from "react";
import { SearchCSVProps } from "./types";

/**
 * SearchCSV component handles searching data loaded in the REPL.
 * @param loadedData - The loaded data to search within.
 */
const SearchCSV: React.FC<SearchCSVProps> = ({ loadedData }) => {
  // For now, returning a default value
  const defaultValue = "Default Search Value";

  if (!loadedData) {
    return <span>No data loaded. Please use load_file before searching.</span>;
  }

  // Here, you would normally implement your search logic
  // For now, returning the default value
  return <span>{defaultValue}</span>;
};

export default SearchCSV;
