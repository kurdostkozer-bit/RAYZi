import React from 'react';
import { useState } from 'react';
import RealPost from '../component/table/Post';
import FakePost from '../component/table/FakePost';

const MainPost = () => {
  const [type, setType] = useState(() => {
    return localStorage.getItem('postTab') || 'realPost';
  });

  const handleTabChange = (newType) => {
    setType(newType);
    localStorage.setItem('postTab', newType);
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
                type === 'realPost' ? 'btn-success' : 'disabledBtn'
              }`}
              onClick={() => handleTabChange('realPost')}
            >
              <span className="">Real Post</span>
            </button>
            <button
              type="button"
              className={`btn btn-sm ${
                type === 'fakePost' ? 'btn-danger' : 'disabledBtn'
              } ms-3`}
              onClick={() => handleTabChange('fakePost')}
            >
              <span className="">Fake Post</span>
            </button>
          </div>
        </div>
      </div>
      {type === 'realPost' ? (
        <RealPost type={type} />
      ) : (
        type === 'fakePost' && <FakePost type={type} />
      )}
    </div>
  );
};

export default MainPost;
