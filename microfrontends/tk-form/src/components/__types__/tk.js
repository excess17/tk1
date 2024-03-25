import PropTypes from 'prop-types';

export default PropTypes.shape({
  id: PropTypes.number,
  field1: PropTypes.string,
  field2: PropTypes.number,
});

export const formValues = PropTypes.shape({
  id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  field1: PropTypes.string,
  field2: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
});

export const formTouched = PropTypes.shape({
  id: PropTypes.oneOfType([PropTypes.bool, PropTypes.shape()]),
  field1: PropTypes.oneOfType([PropTypes.bool, PropTypes.shape()]),
  field2: PropTypes.oneOfType([PropTypes.bool, PropTypes.shape()]),
});

export const formErrors = PropTypes.shape({
  id: PropTypes.oneOfType([PropTypes.string, PropTypes.shape()]),
  field1: PropTypes.oneOfType([PropTypes.string, PropTypes.shape()]),
  field2: PropTypes.oneOfType([PropTypes.string, PropTypes.shape()]),
});