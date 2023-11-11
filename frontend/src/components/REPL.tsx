import { useState } from "react";
import "../styles/main.css";
import { REPLHistory } from "./REPLHistory";
import { REPLInput } from "./REPLInput";

/* 
  This is the REPL's top level component, enabling rendering of all REPL components
*/

export default function REPL() {
  const [history, setHistory] = useState<string[][][]>([]);
  const [scrollPosition, setScrollPosition] = useState(0);

  // This renders the history as well as current input
  return (
    <div className="repl">
      <REPLHistory history={history} />
      <hr></hr>
      <REPLInput
        history={history}
        setHistory={setHistory}
        scrollPosition={scrollPosition}
        setScrollPosition={setScrollPosition}
      />
    </div>
  );
}
