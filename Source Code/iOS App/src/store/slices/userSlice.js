import {createSlice} from '@reduxjs/toolkit';

const initialState = {
  profile: null,
  statistics: null,
  loading: false,
  error: null,
};

const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    fetchProfileStart: state => {
      state.loading = true;
      state.error = null;
    },
    fetchProfileSuccess: (state, action) => {
      state.loading = false;
      state.profile = action.payload;
      state.error = null;
    },
    fetchProfileFailure: (state, action) => {
      state.loading = false;
      state.error = action.payload;
    },
    updateProfileStart: state => {
      state.loading = true;
      state.error = null;
    },
    updateProfileSuccess: (state, action) => {
      state.loading = false;
      state.profile = {...state.profile, ...action.payload};
      state.error = null;
    },
    updateProfileFailure: (state, action) => {
      state.loading = false;
      state.error = action.payload;
    },
    fetchStatisticsStart: state => {
      state.loading = true;
      state.error = null;
    },
    fetchStatisticsSuccess: (state, action) => {
      state.loading = false;
      state.statistics = action.payload;
      state.error = null;
    },
    fetchStatisticsFailure: (state, action) => {
      state.loading = false;
      state.error = action.payload;
    },
    clearError: state => {
      state.error = null;
    },
  },
});

export const {
  fetchProfileStart,
  fetchProfileSuccess,
  fetchProfileFailure,
  updateProfileStart,
  updateProfileSuccess,
  updateProfileFailure,
  fetchStatisticsStart,
  fetchStatisticsSuccess,
  fetchStatisticsFailure,
  clearError,
} = userSlice.actions;

export default userSlice.reducer;