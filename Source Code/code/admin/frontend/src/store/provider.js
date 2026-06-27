import { createStore, compose, applyMiddleware } from "redux";
import thunkMiddleware from "redux-thunk";
import { persistStore, persistReducer } from "redux-persist";
import storage from "redux-persist/lib/storage"; // Default storage for web
import rootReducer from "./index"; // Adjust the path to your root reducer

// Persist Config
const persistConfig = {
  key: "root",
  storage,
  whitelist: ["user", "admin"], // Specify reducers to persist
};

// Create Persisted Reducer
const persistedReducer = persistReducer(persistConfig, rootReducer);

// Redux DevTools
const composeEnhancer =
  typeof window === "object" && window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__
    ? window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__({})
    : compose;

// Create Store
const store = createStore(
  persistedReducer,
  composeEnhancer(applyMiddleware(thunkMiddleware))
);

// Persistor for Redux Persist
const persistor = persistStore(store);

export { store, persistor };
