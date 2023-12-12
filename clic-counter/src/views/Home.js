import React from 'react';

function Home({
  onIncrement,
  onDecrement,
  counter,
}) {
  return (
    <div>
        Compteur: {counter}<br />
        <button onClick={onIncrement}>
            incrémenter
        </button>
        &nbsp;
        <button onClick={onDecrement}>
            décrémenter
        </button>
    </div>
  );
};

export default Home;
