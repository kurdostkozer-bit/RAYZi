import React from 'react';
import { useState } from 'react';
import CoinPlan from '../component/table/CoinPlan';
import VIPPlan from '../component/table/VIPPlan';

const MainPlan = () => {
  const [type, setType] = useState(() => {
    return localStorage.getItem('planTab') || 'coinPlan';
  });

  const handleTabChange = (newType) => {
    setType(newType);
    localStorage.setItem('planTab', newType);
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
                type === 'coinPlan' ? 'btn-success' : 'disabledBtn'
              }`}
              onClick={() => handleTabChange('coinPlan')}
            >
              <span className="">Coin Plan</span>
            </button>
            <button
              type="button"
              className={`btn btn-sm ${
                type === 'vipPlan' ? 'btn-danger' : 'disabledBtn'
              } ms-3`}
              onClick={() => handleTabChange('vipPlan')}
            >
              <span className="">VIP Plan</span>
            </button>
          </div>
        </div>
      </div>
      {type === 'coinPlan' ? (
        <CoinPlan type={type} />
      ) : (
        type === 'vipPlan' && <VIPPlan type={type} />
      )}
    </div>
  );
};

export default MainPlan;
