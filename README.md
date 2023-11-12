# maps-svacanar-kkilinc

Sprint 5 - CS32

### Github Repository

https://github.com/sxfiavn/maps-svacanar-kkilinc

### Authors

- Sofia Vaca Narvaja
- Kaan Kilinc

### Collaborators

- Peter Madoff
- Mason Lee (starter code + caching implementation)
- CS32 Staff (Maps Gearup)

## File Structure

1. index.tsx: Main entry point of the application.
2. main.ts: Core functionality logic.
3. components/: Directory containing all React components.
4. mocked/: Directory containing mocked data in JSON format.
5. styles/: Directory containing all the stylesheets.

- `App.tsx`: The main application component that organizes the application's structure.
- `REPLHistory`: Maintains a history of user commands and their results.
- `REPLInput`: Handles user input, file loading, and command execution.
- `LoadCSV`: Loads CSV files and provides data to the app.
- `ViewCSV`: Displays the loaded CSV data.
- `mockedJson.ts`: Contains mocked data and search functionality.

1. `load`:
   This function is responsible for loading a CSV file. The function expects the file path as the first argument. Once the file path is provided, the function uses an external API to load the data. If successful, it updates the application state with the loaded data.

2. `view`:
   The view function is tasked with rendering the loaded CSV data. If no data is loaded, it will fetch the data using the ViewCSVApiCall function. If data is loaded, it directly renders the data using the ViewCSV component.

3. `search`:
   The search function allows users to search within the loaded CSV data. It expects a search term and other optional parameters that define the search criteria. The function then uses the SearchCSVWrapper component to perform the search and render the results.

4. `mode`:
   This function enables users to switch between different REPL modes like "verbose" and "brief". It expects a mode value as an argument and updates the application mode based on the provided value.

5. `broadband`:
   This function fetches broadband data for a specific state and county. The state and county values are expected as arguments. The function then uses the broadbandAPI function to fetch and display the data.

6. `mapKey`:
   This function displays a key for the colors in the map and what each of it means.

7. `filter`:
   This function filters the map by the given coordinates.

8. `search_area`:
   This function highlights the area in the map with the keyword in the description.

## Running Instructions

### Frontend

- In terminal run "npm run dev" to open web application.

### Backend

- In IntelliJ, build project from backend folder and run serverMain.java
- Declare wether you want to use caching or not for GCS API (broad_band) and GeoJSON (filter and search_area)

### Testing

- Testing of previous Sprints are included
- Integration testing:

## Reflection

What we used: \n

- Hardware: RAM, CPU,
- Back end: IntelliJ IDEA, Node.js, GeoJSON,
- Front end: VS Code, React,
- Fullstack: Playwright, Github, Git, Typescript, npm,
