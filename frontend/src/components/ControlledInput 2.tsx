import "../styles/main.css";
import { Dispatch, SetStateAction } from "react";

// Remember that parameter names don't necessarily need to overlap;
// I could use different variable names in the actual function.
interface ControlledInputProps {
  value: string;
  // This type comes from React+TypeScript. VSCode can suggest these.
  //   Concretely, this means "a function that sets a state containing a string"
  setValue: Dispatch<SetStateAction<string>>;
  ariaLabel: string;
  ariaDescription: string;
  onEnter: () => void;
  onScroll: (key: string) => void;
}

// Input boxes contain state. We want to make sure React is managing that state,
//   so we have a special component that wraps the input box.
export function ControlledInput({
  value,
  setValue,
  ariaLabel,
  ariaDescription,
  onEnter,
  onScroll,
}: ControlledInputProps) {
  const KeyPress = (event: React.KeyboardEvent<HTMLInputElement>) => {
    // User Story 1: Keyboard Shortcut for mouse-less use
    if (event.key === "Enter") {
      // Submit the command
      onEnter();
    } else if (event.key === "ArrowUp" || event.key === "ArrowDown") {
      // Move page up and down
      onScroll(event.key);
    }
  };
  return (
    <input
      type="text"
      className="repl-command-box"
      value={value}
      placeholder="Enter command here!"
      onChange={(ev) => setValue(ev.target.value)}
      aria-label={ariaLabel}
      aria-describedby={ariaDescription}
      onKeyDown={KeyPress}
      onScroll={KeyPress}
    ></input>
  );
}
