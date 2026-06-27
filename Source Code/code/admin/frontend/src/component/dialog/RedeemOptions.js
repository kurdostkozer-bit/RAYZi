import React, { useEffect, useState } from 'react';

//redux
import { connect, useDispatch, useSelector } from 'react-redux';

//MUI
import {
  Dialog,
  DialogContent,
  DialogTitle,
  IconButton,
  Tooltip,
} from '@mui/material';
import { Cancel } from '@mui/icons-material';

//types

//action
import {
  createRedeemOption,
  editRedeemOption,
} from '../../store/redeemOptions/action';

import { CLOSE_REDEEM_OPT_DIALOG } from '../../store/redeemOptions/types';

const RedeeemOptDialog = (props) => {
  const dispatch = useDispatch();

  const { dialog: open, dialogData } = useSelector(
    (state) => state.redeemOption
  );



  const [mongoId, setMongoId] = useState('');
  const [name, setName] = useState('');

  const [errors, setError] = useState({
    name: '',
    coin: '',
    image: '',
  });

  useEffect(() => {
    if (dialogData) {
      setMongoId(dialogData._id);
      setName(dialogData.name);
    }
  }, [dialogData]);

  useEffect(
    () => () => {
      setError({
        name: '',
      });
      setMongoId('');
      setName('');
    },
    [open]
  );

  useEffect(() => {
    window.onbeforeunload = closePopup();
  }, []);

  const closePopup = () => {
    dispatch({ type: CLOSE_REDEEM_OPT_DIALOG });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!name) {
      const errors = {};
      if (!name) errors.name = 'Redeem Option is Required!';

      return setError({ ...errors });
    }


    const data = {
      name: name,
    };
    if (mongoId) {
      props.editRedeemOption({ ...data, id: mongoId });
    } else {
      props.createRedeemOption(data);
    }
  };

  return (
    <>
      <Dialog
        open={open}
        aria-labelledby="responsive-dialog-title"
        onClose={closePopup}
        disableBackdropClick
        disableEscapeKeyDown
        fullWidth
        maxWidth="xs"
      >
        <DialogTitle id="responsive-dialog-title">
          <span className="text-danger font-weight-bold h4">
            {' '}
            Redeem Option{' '}
          </span>
        </DialogTitle>

        <IconButton
          style={{
            position: 'absolute',
            right: 0,
          }}
        >
          <Tooltip title="Close">
            <Cancel className="text-danger" onClick={closePopup} />
          </Tooltip>
        </IconButton>
        <DialogContent>
          <div className="modal-body pt-1 px-1 pb-3">
            <div className="d-flex flex-column">
              <form>
                <div className="form-group">
                  <label className="mb-2 text-gray">Name</label>
                  <input
                    type="text"
                    className="form-control"
                    required=""
                    placeholder="Enter Name"
                    value={name}
                    onChange={(e) => {
                      setName(e.target.value);
                      if (!e.target.value) {
                        return setError({
                          ...errors,
                          name: 'Name is Required!',
                        });
                      } else {
                        return setError({
                          ...errors,
                          name: '',
                        });
                      }
                    }}
                  />
                  {errors.name && (
                    <div className="ml-2 mt-1">
                      {errors.name && (
                        <div className="pl-1 text__left">
                          <span className="text-red">{errors.name}</span>
                        </div>
                      )}
                    </div>
                  )}
                </div>

                <div className={'mt-5'}>
                  <button
                    type="button"
                    className="btn btn-outline-info ml-2 btn-round float__right icon_margin"
                    onClick={closePopup}
                  >
                    Close
                  </button>
                  <button
                    type="button"
                    className="btn btn-round float__right btn-danger"
                    onClick={handleSubmit}
                  >
                    Submit
                  </button>
                </div>
              </form>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
};

export default connect(null, { createRedeemOption, editRedeemOption })(
  RedeeemOptDialog
);
