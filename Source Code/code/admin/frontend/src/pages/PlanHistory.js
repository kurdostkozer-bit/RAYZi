import React from 'react';
import { useState } from 'react';
import CoinPlanHistory from '../component/table/PurchaseCoinPlanHistory';
import VIPPlanHistory from '../component/table/PurchaseVipPlanHistory';

const PlanHistory = () => {
  const [type, setType] = useState(() => {
    return localStorage.getItem('planHistoryTab') || 'coinPlanHistory';
  });

  const handleTabChange = (newType) => {
    setType(newType);
    localStorage.setItem('planHistoryTab', newType);
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
                type === 'coinPlanHistory' ? 'btn-success' : 'disabledBtn'
              }`}
              onClick={() => handleTabChange('coinPlanHistory')}
            >
              <span className="">Coin Plan History</span>
            </button>
            <button
              type="button"
              className={`btn btn-sm ${
                type === 'vipPlanHistory' ? 'btn-danger' : 'disabledBtn'
              } ms-3`}
              onClick={() => handleTabChange('vipPlanHistory')}
            >
              <span className="">VIP Plan History</span>
            </button>
          </div>
        </div>
      </div>
      {type === 'coinPlanHistory' ? (
        <CoinPlanHistory type={type} />
      ) : (
        type === 'vipPlanHistory' && <VIPPlanHistory type={type} />
      )}
    </div>
  );
};

export default PlanHistory;
