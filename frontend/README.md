## Installation

Install the ImageFinder frontend with npm

```bash
  npm install
```

You can then build it using 
```bash
npm run build
```
This will build the frontend at paste it into the backend's webapp folder allowing JettyServer to serve it to the user on localhost:8080

## Structure

The frontend is made using React. It has only 2 components:
- **ImageSearchForm** - This includes the URL search bar, the search options as well as the submit button.
- **ImageList** - This contains the tabs separating the different types of images as well as the images themselves contained within each tab.