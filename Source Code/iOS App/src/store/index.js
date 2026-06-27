import {configureStore} from '@reduxjs/toolkit';
import {persistStore, persistReducer, FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER} from 'redux-persist';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {combineReducers} from '@reduxjs/toolkit';

// Slices
import authSlice from './slices/authSlice';
import userSlice from './slices/userSlice';
import gameSlice from './slices/gameSlice';
import walletSlice from './slices/walletSlice';
import socketSlice from './slices/socketSlice';

// Persist configuration
const persistConfig = {
  key: 'root',
  storage: AsyncStorage,
  whitelist: ['auth', 'user', 'wallet'], // حفظ البيانات الأساسية والمالية بعد إغلاق التطبيق
  blacklist: ['socket', 'game'],        // استبعاد قنوات الاتصال الحي ليعاد بناؤها عند الفتح
};

// Root reducer
const rootReducer = combineReducers({
  auth: authSlice,
  user: userSlice,
  game: gameSlice,
  wallet: walletSlice,
  socket: socketSlice,
});

// Persisted reducer
const persistedReducer = persistReducer(persistConfig, rootReducer);

// Configure store
const store = configureStore({
  reducer: persistedReducer,
  middleware: getDefaultMiddleware =>
    getDefaultMiddleware({
      serializableCheck: {
        // تعديل تقني حاسم: تضمين كافة أفعال redux-persist لمنع تحذيرات الـ Serialization وتحسين الأداء
        ignoredActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER],
      },
    }),
  devTools: __DEV__,
});

// Persistor
const persistor = persistStore(store);

export {store, persistor};
