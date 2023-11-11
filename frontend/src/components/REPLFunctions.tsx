// import React, { useState } from "react";
// import { GeoJSONContext } from "./GeoJSONContext";
// import { REPLInput } from "./REPLInput";
// import "../styles/App.css"; // Import your styles here

// interface REPLFunction {
//   (args: Array<string>): Promise<string[][]>;
// }

// // Swaps between brief and verbose forms of output when the mode command is
// // submitted
// const changeMode: REPLFunction = async (command: string[]) => {
//   if (mode) {
//     setMode(!mode);
//     return [["Application has been set to brief mode"]];
//   } else {
//     setMode(!mode);
//     return [["Application has been set to verbose mode"]];
//   }
// };

// // filters the geojson according to a bounding box.
// const filter: REPLFunction = async (command: string[]) => {
//   if (command.length < 5) {
//     return [["Please input a complete bounding box."]];
//   } else {
//     try {
//       const response = await fetch(
//         "http://localhost:8080/filter?lowerX=" +
//           command[1] +
//           "&upperX=" +
//           command[2] +
//           "&lowerY=" +
//           command[3] +
//           "&upperY=" +
//           command[4]
//       );
//       const data = await response.json();
//       if (data["result"] == "success") {
//         console.log(data["retrieval_time"]);
//         addOutput("filter", [
//           ["Retrived from server:"],
//           [data["retrieval_time"]],
//         ]);

//         if (data["featuresInBox"].length < 2) {
//           return [["No data fits the given bounds."]];
//         } else {
//           console.log(data["featuresInBox"]);
//           return data["featuresInBox"];
//         }
//       } else {
//         return [["The server was unable to obtain a response."]];
//       }
//     } catch (error) {
//       console.error("Fetch error:", error);
//       return [["There was an error fetching the bounding box data."]];
//     }
//   }
// };

// // Function to see key of the colors in map
// const mapKey: REPLFunction = async (command: string[]) => {
//   return [["Map Key:", "Green: A", "Blue: B", "Yellow: C", "Red: D"]];
// };

// // Fetches a broadband request from the backend server when a broadband command is
// // submitted
// const broadband: REPLFunction = async (command: string[]) => {
//   if (typeof command[1] == "undefined" || typeof command[2] == "undefined") {
//     return [["Please input a state and a county."]];
//   } else {
//     const fetch1 = await fetch(
//       "http://localhost:8080/broadband?state=" +
//         command[1] +
//         "&county=" +
//         command[2]
//     );
//     const json1 = await fetch1.json();
//     const result = await json1["result"];
//     if (result === "success") {
//       const data = json1["data"];
//       const percentage = data["percentage"];
//       const time = data["retrievalTime"];

//       if (typeof percentage === "undefined") {
//         return [["No data returned from API"]];
//       } else {
//         return [
//           [
//             percentage +
//               " percent of houses in " +
//               command[2] +
//               " County, " +
//               command[1] +
//               " have broadband internet. ",
//           ],
//           ["This data was retrieved from ACS on " + time],
//         ];
//       }
//     } else if (result == "error_bad_request") {
//       const error_message = await json1["details of error"];
//       return [
//         [
//           "Please input a county and state in the correct order [state county].",
//         ],
//       ];
//     } else if (result == "error_bad_json") {
//       const error_message = await json1["details of error"];
//       return [[result], [error_message]];
//     } else if (result == "error_datasource") {
//       const error_message = await json1["details of error"];
//       return [[result], [error_message]];
//     } else if (result == "invalid_input") {
//       return [["Could not find city or state"]];
//     } else {
//       return [["Server is busy."]];
//     }
//   }
// };

// // This function is called when the view command is submitted and accesses
// // a loaded csv file to be shown as the output.
// const view: REPLFunction = async (command: string[]) => {
//   if (fileLoaded) {
//     return fetch("http://localhost:8080/viewcsv")
//       .then((r) => r.json())
//       .then((r) => {
//         if (r["result"] == "success") {
//           console.log(r["data"].length);
//           if (r["data"].length === 0) {
//             return [["File is empty!"]];
//           }
//           return r["data"];
//         }
//       });
//   } else {
//     return [["There is no file loaded"]];
//   }
// };

// // This function handles mocked search functionality. Checks for if the search
// // parameters leads to one of our projected results. Returns informative errors.

// // This function handles search commands by making a search request to the backend
// // server
// const search: REPLFunction = async (args: string[]) => {
//   if (fileLoaded) {
//     const fetch1 = await fetch(
//       "http://localhost:8080/searchcsv?target=" +
//         args[1] +
//         "&identifier=" +
//         args[2] +
//         "&header=" +
//         args[3]
//     );
//     console.log(fetch1);
//     const json1 = await fetch1.json();
//     const result = await json1["result"];
//     if (result == "success") {
//       const data = await json1["data"];
//       return data;
//     } else if (result == "error_bad_request") {
//       return [["Bad request! Please rewrite your querie(s) and try again."]];
//     } else if (result == "error_datasource") {
//       const error_message = await json1["details of error"];
//       return [[error_message]];
//     }
//   } else {
//     return [["There is no file loaded"]];
//   }
// };

// // This function handles load functionality by making a load request to the backend server
// const load_file: REPLFunction = async (command: string[]) => {
//   if (command[1] == null) {
//     command[1] = " ";
//   }
//   console.log("string", command[1]);
//   return fetch("http://localhost:8080/loadcsv?filepath=" + command[1])
//     .then((r) => r.json())
//     .then((r) => {
//       console.log(r["result"]);
//       if (r["result"] == "success") {
//         setFile(true);
//       } else if (r["result"] == "error_datasource") {
//         return [["This is not a valid filepath!"]];
//       } else if (r["result"] == "error_bad_request") {
//         return [["Please enter a filepath!"]];
//       }
//       return [[r["result"]]];
//     });
// };

// // A map of the registered commands to their corresponding REPLFunction
// export let functions = new Map<string, REPLFunction>();
// functions.set("mode", changeMode);
// functions.set("view", view);
// functions.set("load_file", load_file);
// functions.set("search", search);
// functions.set("broadband", broadband);
// functions.set("filter", filter);
// functions.set("search_area", searchArea);
// functions.set("map_key", mapKey);
