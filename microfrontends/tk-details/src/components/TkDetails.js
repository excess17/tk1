import React from 'react';
import PropTypes from 'prop-types';
import { withTranslation } from 'react-i18next';
import Box from '@material-ui/core/Box';

import tkType from 'components/__types__/tk';
import TkFieldTable from 'components/tk-field-table/TkFieldTable';

const TkDetails = ({ t, tk }) => {
  return (
    <Box>
      <h3 data-testid="details_title">
        {t('common.widgetName', {
          widgetNamePlaceholder: 'Tk',
        })}
      </h3>
      <TkFieldTable tk={tk} />
    </Box>
  );
};

TkDetails.propTypes = {
  tk: tkType,
  t: PropTypes.func.isRequired,
};

TkDetails.defaultProps = {
  tk: {},
};

export default withTranslation()(TkDetails);
