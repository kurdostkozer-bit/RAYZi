import React from "react";

const UserGameHistory = (props) => {
  return (
    <table className="table table-striped mt-5">
      <thead>
        <tr>
          <th>No.</th>
          <th>Type</th>
          <th>Diamond</th>
          <th>Date</th>
          <th>Time</th>
        </tr>
      </thead>
      <tbody>
        {props.data?.length > 0 ? (
          props.data.map((data, index) => {
            var gameDate = data?.date ? data?.date.split(",") : [];
            return (
              <tr key={index}>
                <td>{index + 1}</td>

                {data?.type === 10 && <td>TeenPatti_Game</td>}
                {data?.type === 15 && <td>Roulette_Game</td>}
                {data?.type === 16 && <td>FerryWheel_Game</td>}
                {data?.type === 17 && <td>CrashRocket_Game</td>}

                {data.diamond ? (
                  data.income ? (
                    data.diamond !== 0 ? (
                      <td className="text-success text-center">
                        +{data.diamond}
                      </td>
                    ) : (
                      <td>{data.diamond}</td>
                    )
                  ) : data.diamond !== 0 ? (
                    <td className="text-red text-center">-{data.diamond}</td>
                  ) : (
                    <td>{data.diamond}</td>
                  )
                ) : (
                  <td>-</td>
                )}
                <td className="text-info">{gameDate[0]}</td>
                <td className="text-primary">{gameDate[1]}</td>

                <td>{data.purchaseDate}</td>
              </tr>
            );
          })
        ) : (
          <tr>
            <td colSpan="8" align="center">
              Nothing to show!!
            </td>
          </tr>
        )}
      </tbody>
    </table>
  );
};

export default UserGameHistory;
