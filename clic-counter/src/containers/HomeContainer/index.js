import React from 'react';
import { connect } from 'react-redux';
import { compose } from 'redux';
import { createStructuredSelector } from 'reselect';

import Home from '../../views/Home';
import { makeSelectHomeContainerCounter } from './selector';
import {decrementAction, incrementAction} from '../../counter/action';
import { useInjectReducer } from '../../utils/injectReducer';

import reducer from '../../counter/reducer';

const key = 'homeContainer';

function HomeContainer(props) {
  useInjectReducer({ key, reducer });

  return (<Home {...props} />);
}

const mapStateToProps = createStructuredSelector({
  counter: makeSelectHomeContainerCounter(),
});

export function mapDispatchToProps(dispatch) {
  return {
    dispatch,
    onIncrement: () => dispatch(incrementAction()),
    onDecrement: () => dispatch(decrementAction()),
  };
}

const withConnect = connect(
  mapStateToProps,
  mapDispatchToProps,
);

export default compose(
  withConnect,
)(HomeContainer);

