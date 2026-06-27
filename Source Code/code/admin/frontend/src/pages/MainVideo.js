import React from 'react';
import { useState } from 'react';
import RealVideo from '../component/table/Video';
import FakeVideo from '../component/table/FakeVideo';

const MainVideo = () => {
  const [type, setType] = useState(() => {
    return localStorage.getItem('videoTab') || 'realVideo';
  });

  const handleTabChange = (newType) => {
    setType(newType);
    localStorage.setItem('videoTab', newType);
  };
  return (
    <div>
      {/* Tabs  */}
      <div className="row mb-3">
        <div className="col-12">
          <div className="my-2">
            <button
              type="button"
              className={`btn btn-sm ${
                type === 'realVideo' ? 'btn-success' : 'disabledBtn'
              }`}
              onClick={() => handleTabChange('realVideo')}
            >
              <span className="">Real Video</span>
            </button>
            <button
              type="button"
              className={`btn btn-sm ${
                type === 'fakeVideo' ? 'btn-danger' : 'disabledBtn'
              } ms-3`}
              onClick={() => handleTabChange('fakeVideo')}
            >
              <span className="">Fake Video</span>
            </button>
          </div>
        </div>
      </div>
      {type === 'realVideo' ? (
        <RealVideo type={type} />
      ) : (
        type === 'fakeVideo' && <FakeVideo type={type} />
      )}
    </div>
  );
};

export default MainVideo;
