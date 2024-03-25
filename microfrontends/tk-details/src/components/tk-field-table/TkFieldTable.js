import React from 'react';
import PropTypes from 'prop-types';
import { withTranslation } from 'react-i18next';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';

import tkType from 'components/__types__/tk';

const TkFieldTable = ({ t, tk }) => (
  <Table>
    <TableHead>
      <TableRow>
        <TableCell>{t('common.name')}</TableCell>
        <TableCell>{t('common.value')}</TableCell>
      </TableRow>
    </TableHead>
    <TableBody>
      <TableRow>
        <TableCell>
          <span>{t('entities.tk.id')}</span>
        </TableCell>
        <TableCell>
          <span>{tk.id}</span>
        </TableCell>
      </TableRow>
      <TableRow>
        <TableCell>
          <span>{t('entities.tk.field1')}</span>
        </TableCell>
        <TableCell>
          <span>{tk.field1}</span>
        </TableCell>
      </TableRow>
      <TableRow>
        <TableCell>
          <span>{t('entities.tk.field2')}</span>
        </TableCell>
        <TableCell>
          <span>{tk.field2}</span>
        </TableCell>
      </TableRow>
    </TableBody>
  </Table>
);

TkFieldTable.propTypes = {
  tk: tkType,
  t: PropTypes.func.isRequired,
};

TkFieldTable.defaultProps = {
  tk: [],
};

export default withTranslation()(TkFieldTable);
