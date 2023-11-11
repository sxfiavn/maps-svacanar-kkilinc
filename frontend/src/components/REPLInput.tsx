import "../styles/main.css";
import { Dispatch, SetStateAction, useState, useEffect, useRef } from "react";
import { ControlledInput } from "./ControlledInput";
import { useContext } from "react";
import { GeoJSONContext } from "./GeoJSONContext";
import { functions } from "./REPLFunctions";

// This interface contains the history (a list of 2D Arrays)
// and a setHistory function
interface REPLInputProps {
  history: string[][][];
  setHistory: Dispatch<SetStateAction<string[][][]>>;
  scrollPosition: number;
  setScrollPosition: React.Dispatch<React.SetStateAction<number>>;
}

// Class that handles when text input is sent to the server.
export function REPLInput(props: REPLInputProps) {
  //manage focus - prevents keyevents when on the input bar
  let input = useRef<HTMLInputElement>(null);
  let [commandString, setCommandString] = useState<string>("");
  let [mode, setMode] = useState<boolean>(false);
  let [fileLoaded, setFile] = useState<boolean>(false);
  const geoJSONContext = useContext(GeoJSONContext);
  let [scrollPosition, setScrollPosition] = useState<number>(0);

  // This function is triggered when the button is clicked to handle whatever
  // command is sent.
  async function handleSubmit(commandString: string) {
    let command: string[] | null = commandString.match(/\[.*?\]|\S+/g);
    //check that input is not "empty"
    if (command != null) {
      command = command.map((string) => string.replace(/[\[\]]/g, ""));
      const fn = functions.get(command[0]);
      if (fn) {
        addOutput(commandString, await fn(command));
      } else {
        addOutput(commandString, [["Please enter a registered command"]]);
      }
      setCommandString("");
    }
  }

  // This function is triggered when the up and down arrows are clicked to scroll
  const handleScroll = (scrollDirection: string) => {
    if (scrollDirection === "ArrowUp") {
      // Scroll up by a certain amount (you can adjust this value)
      props.setScrollPosition(props.scrollPosition - 500);
    } else if (scrollDirection === "ArrowDown") {
      // Scroll down by a certain amount (you can adjust this value)
      props.setScrollPosition(props.scrollPosition + 500);
    }

    // Use window.scrollTo to set the scroll position
    window.scrollTo({
      top: props.scrollPosition,
      behavior: "smooth", // You can use "auto" for instant scrolling
    });
  };

  // This function adds a response to history. If the mode is true the response
  // will be added in verbose output, if false in brief.
  function addOutput(commandString: string, output: string[][]) {
    if (mode) {
      props.setHistory((prevHistory) => [
        ...prevHistory,
        [["Command: " + commandString], ["Output: "]],
        output,
      ]);
    } else {
      props.setHistory((prevHistory) => [...prevHistory, output]);
    }
  }

  interface REPLFunction {
    (args: Array<string>): Promise<string[][]>;
  }

  // Swaps between brief and verbose forms of output when the mode command is
  // submitted
  const changeMode: REPLFunction = async (command: string[]) => {
    if (mode) {
      setMode(!mode);
      return [["Application has been set to brief mode"]];
    } else {
      setMode(!mode);
      return [["Application has been set to verbose mode"]];
    }
  };

  // Function to filter the geojson according to 4 boundaries.
  const filter: REPLFunction = async (command: string[]) => {
    if (command.length < 5) {
      return [["Error: No bounding box given."]];
    } else {
      try {
        const response = await fetch(
          "http://localhost:8080/filter?lowerX=" +
            command[1] +
            "&upperX=" +
            command[2] +
            "&lowerY=" +
            command[3] +
            "&upperY=" +
            command[4]
        );

        const { result, retrieval_time, featuresInBox } = await response.json();

        if (result == "success") {
          addOutput("filter", [["Retrieved from server:"], [retrieval_time]]);

          if (featuresInBox.length >= 2) {
            return featuresInBox;
          } else {
            return [["No data fits the given bounds."]];
          }
        } else {
          return [["No response obtained from server"]];
        }
      } catch (error) {
        console.error("Fetch error:", error);
        return [["Error: Couldn't fetch the data."]];
      }
    }
  };

  // Function to see key of the colors in map
  const mapKey: REPLFunction = async (command: string[]) => {
    return [["Map Key:", "Green: A", "Blue: B", "Yellow: C", "Red: D"]];
  };

  // Fetches a broadband request from the backend server when a broadband command is
  // submitted
  const broadband: REPLFunction = async (command: string[]) => {
    if (typeof command[1] == "undefined" || typeof command[2] == "undefined") {
      return [["Please input a state and a county."]];
    } else {
      const fetch1 = await fetch(
        "http://localhost:8080/broadband?state=" +
          command[1] +
          "&county=" +
          command[2]
      );
      const json1 = await fetch1.json();
      const result = await json1["result"];
      if (result === "success") {
        const data = json1["data"];
        const percentage = data["percentage"];
        const time = data["retrievalTime"];

        if (typeof percentage === "undefined") {
          return [["No data returned from API"]];
        } else {
          return [
            [
              percentage +
                " percent of houses in " +
                command[2] +
                " County, " +
                command[1] +
                " have broadband internet. ",
            ],
            ["This data was retrieved from ACS on " + time],
          ];
        }
      } else if (result == "error_bad_request") {
        const error_message = await json1["details of error"];
        return [
          [
            "Please input a county and state in the correct order [state county].",
          ],
        ];
      } else if (result == "error_bad_json") {
        const error_message = await json1["details of error"];
        return [[result], [error_message]];
      } else if (result == "error_datasource") {
        const error_message = await json1["details of error"];
        return [[result], [error_message]];
      } else if (result == "invalid_input") {
        return [["Could not find city or state"]];
      } else {
        return [["Server is busy."]];
      }
    }
  };

  // This function is called when the view command is submitted and accesses
  // a loaded csv file to be shown as the output.
  const view: REPLFunction = async (command: string[]) => {
    if (fileLoaded) {
      return fetch("http://localhost:8080/viewcsv")
        .then((r) => r.json())
        .then((r) => {
          if (r["result"] == "success") {
            console.log(r["data"].length);
            if (r["data"].length === 0) {
              return [["File is empty!"]];
            }
            return r["data"];
          }
        });
    } else {
      return [["There is no file loaded"]];
    }
  };

  // This function handles mocked search functionality. Checks for if the search
  // parameters leads to one of our projected results. Returns informative errors.

  // This function handles search commands by making a search request to the backend
  // server
  const search: REPLFunction = async (args: string[]) => {
    if (fileLoaded) {
      const fetch1 = await fetch(
        "http://localhost:8080/searchcsv?target=" +
          args[1] +
          "&identifier=" +
          args[2] +
          "&header=" +
          args[3]
      );
      console.log(fetch1);
      const json1 = await fetch1.json();
      const result = await json1["result"];
      if (result == "success") {
        const data = await json1["data"];
        return data;
      } else if (result == "error_bad_request") {
        return [["Bad request! Please rewrite your querie(s) and try again."]];
      } else if (result == "error_datasource") {
        const error_message = await json1["details of error"];
        return [[error_message]];
      }
    } else {
      return [["There is no file loaded"]];
    }
  };

  // This function handles load functionality by making a load request to the backend server
  const load_file: REPLFunction = async (command: string[]) => {
    if (command[1] == null) {
      command[1] = " ";
    }
    console.log("string", command[1]);
    return fetch("http://localhost:8080/loadcsv?filepath=" + command[1])
      .then((r) => r.json())
      .then((r) => {
        console.log(r["result"]);
        if (r["result"] == "success") {
          setFile(true);
        } else if (r["result"] == "error_datasource") {
          return [["This is not a valid filepath!"]];
        } else if (r["result"] == "error_bad_request") {
          return [["Please enter a filepath!"]];
        }
        return [[r["result"]]];
      });
  };

  // A map of the registered commands to their corresponding REPLFunction
  let functions = new Map<string, REPLFunction>();
  functions.set("mode", changeMode);
  functions.set("view", view);
  functions.set("load_file", load_file);
  functions.set("search", search);
  functions.set("broadband", broadband);
  functions.set("filter", filter);
  functions.set("map_key", mapKey);

  return (
    <div className="repl-input">
      <fieldset>
        <legend>Enter a command:</legend>
        <ControlledInput
          value={commandString}
          setValue={setCommandString}
          ariaLabel={"Command input"}
          ariaDescription="Text box for entering commands"
          onEnter={() => handleSubmit(commandString)} // Won't need to click Submit for it to run command
          onScroll={handleScroll} // Can scroll up/down with arrow keys
        />
      </fieldset>
      <button onClick={() => handleSubmit(commandString)}>Submit</button>
    </div>
  );
}
