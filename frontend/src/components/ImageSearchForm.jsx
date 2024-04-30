/* eslint-disable react/prop-types */
import '../App.css';

function ImageSearchForm({
  url, setUrl, fetchImages, loading, error,
  allowIcons, setAllowIcons,
  allowThumbnails, setAllowThumbnails,
  searchDepth, setSearchDepth
}) {
  return (
    <div style={{ width: '100%', maxWidth: '600px', margin: 'auto' }}>
      <form onSubmit={(e) => { e.preventDefault(); fetchImages(); }}>
        <input
          type="text"
          value={url}
          onChange={(e) => setUrl(e.target.value)}
          placeholder="Enter URL here"
          className="input-text"
        />
        <div className="options-container">
          <div className="option">
            <label>
            <input
              type="checkbox"
              checked={allowIcons}
              onChange={() => setAllowIcons(!allowIcons)}
            /> Icons? </label>
          </div>
          <div className="option">
          <label><input
              type="checkbox"
              checked={allowThumbnails}
              onChange={() => setAllowThumbnails(!allowThumbnails)}
            /> Thumbnails? </label>
          </div>
          <div className="option">
            <input
              type="number"
              value={searchDepth}
              onChange={(e) => setSearchDepth(parseInt(e.target.value, 10))}
              className="input-depth"
              min="0"
            /> Search Depth
          </div>
        </div>
        <button type="submit" disabled={loading} className="submit-button">
          {loading ? 'Loading...' : 'Submit!'}
        </button>
        {error && <p className="error">{error}</p>}
      </form>
    </div>
  );
}

export default ImageSearchForm;
